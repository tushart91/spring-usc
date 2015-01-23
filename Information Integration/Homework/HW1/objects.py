#!/usr/bin/env python

class Dimensions:

    def __init__(self, height=0, width=0, depth=0):
        self.height = height
        self.width = width
        self.depth = depth


class Place:

    def __init__(self, country='', state='', province=''):
        self.country = country
        self.state = state
        self.province = province


                class Author:

                    def __init__(
                        self,
                        firstname,
                        lastname,
                        place_active,
                        place_born,
                        dob,
                        dod,
                        link,
                    ):
                        self.firstname = firstname
                        self.lastname = lastname
                        self.place_active = place_active
                        self.place_born = place_born
                        self.dob = dob
                        self.dod = dod
                        self.link = link


                        class Painting:

                            def __init__(
                                self,
                                id,
                                name,
                                author,
                                place,
                                date,
                                dimensions,
                                acquired,
                                link,
                                img_link,
                            ):
                                self.id = id
                                self.name = name
                                self.author = author
                                self.dimensions = dimensions
                                self.type = type
                                self.acquired = acquired
                                self.link = link
                                self.img_link = img_link
