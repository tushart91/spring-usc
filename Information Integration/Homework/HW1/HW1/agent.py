#!/usr/bin/python
# -*- coding: utf-8 -*-

########################################
# @file     agent.py
# @author   Tushar Tiwari
# @email    ttiwari@usc.edu
# @course   Information Integration/HW1
# @prof     Prof. Ambite/Knoblock
# @date     2015-01-17
########################################

from bs4 import BeautifulSoup
import requests
import re
import json
import sys, traceback

ROOT_URL = r"http://collections.lacma.org"

def scrape_artist(artist):
    if not artist:
        return {}
    author = {}
    #Get author link
    if artist.has_attr('href'):
        author['link'] = ROOT_URL + artist['href']
    artist = artist.text

    #Extract Name
    #artist = r'Charles Houghton Howard (United States, Penn, Alleghany, active France, 1856-1945)'
    author_name = artist.split('(')[0].strip(' ').split(' ', 2)
    author['name'] = {}
    if len(author_name) > 0:
        author['name']['first'] = author_name[0]
    if len(author_name) > 1:
        author['name']['last'] = author_name[len(author_name) - 1]
    if len(author_name) > 2:
        author['name']['middle'] = author_name[1]

    #Purify remaining string
    artist = re.sub('\(possibly\)', '', artist)
    if len(artist.split('(')) > 1:
        artist = artist.split('(')[1].strip(')').strip()
    else: return author

    #United States, Pennsylvania, Alleghany, active Russia, 1899-1978
    details = artist.split(',')

    #["United States", "Pennsylvania", "Alleghany", "1899-1978"]
    #Get country of birth
    if len(details) > 0:
        author['birthplace'] = {}
        author['birthplace']['country'] = details[0].strip()
        # print "Author_country:", author['country']
        details = details[1:]

    #Get life span
    #["Pennsylvania", "Alleghany", "active Russia", "1899-1978"]
    if len(details) > 0:
        author['dates'] = details[len(details) - 1].strip()
        author_dates = re.findall(r'[0-9]+-[0-9]+', author['dates'])
        if len(author_dates) > 0:
            author['dates'] = {'born': author_dates[0].split('-')[0], \
                               'died': author_dates[0].split('-')[1]  \
                              }
        else:
            author_dates = re.findall(r'(.*born.*)', author['dates'])
            if len(author_dates) > 0:
                author_dates = re.sub('born', '', author_dates[0].strip()).strip()
                author['dates'] = {'born': author_dates }
        #print "Author_dates:", author['dates']
        details = details[:len(details) - 1]

    #Get active country
    #["Pennsylvania", "Alleghany", "active Russia"]
    if len(details) > 0:
        author_active = re.findall('(.*active .*)', details[len(details) - 1])
        if len(author_active) > 0:
            author['active'] = re.sub('active', '', author_active[0].strip()).strip()
            #print "Author_active:", author['active']
            details = details[:len(details) - 1]

    #Get province
    #["Alleghany"]
    if len(details) > 0:
        author['birthplace']['town'] = details[len(details) - 1].strip()
        # print "Author_town:", author['town']
        details = details[:len(details) - 1]


    #Get state
    #["Pennsylvania", "Alleghany"]
    if len(details) > 0:
        author['birthplace']['state'] = details[0].strip()
        # print "Author_state:", author['state']
        details = details[1:]

    return author

def scrape_painting(name, _id, url, soup, category):
    object = {}
    object['name'] = name
    object['_id']  = _id
    object['link'] = url
    object['category'] = category
    #artist
    artist = soup.find('div', {'class': 'field artist-name'})
    if artist:
        artists = artist.find_all('a')
        if len(artists) == 1:
            object['artist'] = scrape_artist(artists[0])
        elif len(artists) > 1:
            object['artist'] = []
            for artist in artists:
                object['artist'].append(scrape_artist(artist))

    #image-url
    asset_img = soup.find('div', {'class': 'media-asset-image'})
    image_url = asset_img.find('img') if asset_img else None
    if (image_url and image_url.has_attr('src')):
        object['image_link'] = image_url['src'].strip()

    #copyright info
    copyright = soup.find('div', {'class': 'field-name-field-copyright-text'})
    if copyright:
        object['copyright'] = copyright.text.strip("©").strip()

    #on view
    on_view = soup.find('a', {'class': 'map-it-link'})
    object['on_view'] = on_view.previous_sibling.strip() if on_view else 'Not currently on public view'

    #dated
    country_year = soup.find('div', {'class': 'field artist-name'}).next_sibling
    country_year = re.search('([A-Z,a-z, ]*), (.+)', country_year)
    if country_year:
        object['created_country'] = country_year.group(1).strip()
        object['created_year']    = country_year.group(2).strip()
        year = re.search('([0-9]+)', country_year.group(2).strip())
        if year and year.group(1):
            year = year.group(1).strip()
            if len(year) == 4 and int(year) <= 1700:
                return None
            if len(year) == 2 and int(year) <= 17:
                return None

    fields = (soup.find('div', {'class': 'group-right'})).find_all('div', {'class': 'field'})
    for field in fields:
        if len(field['class']) == 1 and field['class'][0] == 'field' and field.text != '':
            #painting type
            if re.search('(.*on canvas.*)', field.text):
                object['painting_type'] = field.text.strip()

            dimensions_str = re.search('([0-9]+\.?[0-9]*) x ([0-9]+\.?[0-9]*)(?: x )?([0-9]+\.?[0-9]*)? cm', field.text)
            if dimensions_str and len(dimensions_str.groups()) >= 2:
                display = re.search('([A-Z,a-z]+):', field.text)
                if display:
                    object['display'] = display.group(1)
                object['dimensions'] = {
                                           'units': 'cm',
                                           'height': dimensions_str.group(1).strip(),
                                           'width': dimensions_str.group(2).strip()
                                       }
                if dimensions_str.group(3):
                    object['dimensions']['depth'] = dimensions_str.group(3).strip()

            if re.search('[^\(]+\([A-Z]*[0-9,\.]+\)', field.text):
                object['acquired_from'] = field.text.split('(')[0].strip()
                object['acquired_id'] = field.text.split('(')[1].strip(') ').strip()

    #big text on bottom
    bottom_soup = soup.find('div', {'id': 'art-content-blocks'})
    bottom_soup = bottom_soup.find_all('div', {'class': 'group'}) if bottom_soup else None
    if bottom_soup:
        for i in bottom_soup:
            title = i.span.text.strip()
            text = i.find('div', {'class': 'group-expanded'})
            if not text:
                text = i.find('div', {'class': 'group-teaser'})
            if not text: continue
            if title == 'Bibliography':
                object[title.lower()] = []
                lis = text.ul.find_all('li')
                for li in lis:
                    object[title.lower()].append(' '.join(li.text.replace('\n','').split()))
            else:
                object[title.lower()] = ' '.join(text.text.replace('\n','').split())

#    print(json.dumps(object, sort_keys = True, indent = 4, ensure_ascii = False))
    return object

def scrape_page(ROOT_URL, hrefs, category):
    coll = []
    for (name, href, _id) in hrefs:
        URL = ROOT_URL + href
#        URL = r"http://collections.lacma.org/node/184481"
        try:
            response = requests.get(URL)
            soup = BeautifulSoup(response.text).find('div',
                    {'id': 'content'})
            result = scrape_painting(name, _id, URL, soup, category)
            if result:
                coll.append(result)
        except:
            print(URL)
            print('-'*60)
            traceback.print_exc(file=sys.stdout)
            print('-'*60)
            print()
#        break
    return coll


def paginate(total, ROOT_URL, URL_1, URL_2, category):
    result = []
    for i in range(total):
        URL = ROOT_URL + URL_1 + str(i) + URL_2
        response = requests.get(URL)
        soup = BeautifulSoup(response.text)
        hrefs = []
        for i in soup.find('table', {'class': 'search-results'
                           }).find_all('div',
                {'class': 'search-result-data'}):
            i = i.find('a')
            hrefs.append([i.text, i['href'], int(i['href'][6:])])

        result.extend(scrape_page(ROOT_URL, hrefs, category))
#        break
    print('Total Paintings:', len(result))

    return result


def main():
    page = 0
    results = []
    QUERY_URL = r"/search/site/?page="
    SEARCH_URLS =[ ['american',r"&f[0]=bm_field_has_image%3Atrue&f[1]=im_field_curatorial_area%3A32&f[2]=im_field_classification%3A22"],['european',r"&f[0]=bm_field_has_image%3Atrue&f[1]=im_field_classification%3A22&f[2]=im_field_curatorial_area%3A41"]]
    for category, SEARCH_URL in SEARCH_URLS:
        URL = ROOT_URL + QUERY_URL + str(page) + SEARCH_URL
        response = requests.get(URL)
        soup = BeautifulSoup(response.text)
        total = int(soup.find('div', {'class': 'search-total'}).text[:3]) \
            // 20 + 1
        results.extend(paginate(total, ROOT_URL, QUERY_URL, SEARCH_URL, category))
#        break
    try:
        print('Combined Total Paintings:', len(results))
        outfile = open('output.json', 'w')
        outfile.write(json.dumps(results, sort_keys = True, indent = 4, ensure_ascii = False))
        outfile.close()
    except:
        print('-'*60)
        traceback.print_exc(file=sys.stdout)
        print('-'*60)
        print()
        exit()
    return


if __name__ == '__main__':
    main()
