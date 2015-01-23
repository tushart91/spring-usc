<?php
  $geoURL = "http://where.yahooapis.com/v1/";
  $appIdStr = "?appid=EdponavV34HiU.GzWTw0LJR_imzTWhlOE3mUqwBoNKhHIcbujH6M2pVgsSGdIBP82b6mXDW.R6CgaYJgR.o.vbcEOlz0K9c-";
  $zipCodeStr = "concordance/usps/";
  $cityStr1 = "places\$and(.q('";
  $cityStr2 = "'),.type(7));start=0;count=1";

  $weatherURL = "http://weather.yahooapis.com/forecastrss?w=";
  $tempFStr = "&u=f";
  $tempCStr = "&u=c";

  if($_GET['type'] == "city"){
      // replace any spaces in a city name with + to make a url
      $cityName = $_GET['location'];
      $cityName = str_replace(' ', '+', $cityName);
      
      $geoURL = $geoURL . $cityStr1 . $cityName . $cityStr2 . $appIdStr;
  }
  else if($_GET['type'] == "zip"){
      $geoURL = $geoURL . $zipCodeStr . $_GET['location'] . $appIdStr;
  }

  $headers = get_headers($geoURL);
  $errors = substr($headers[0], 9, 3);
  
  if($errors != "404"){ // no errors occurred when opening the page	
      $xmlStr = file_get_contents($geoURL);
      $xmlData = simplexml_load_string($xmlStr);

      $woeids = array();
      if($_GET['type'] == "city"){
          for($i = 0; $i < count($xmlData); $i++){
              if($xmlData->place[$i]->woeid){
                 $woeids[$i] = $xmlData->place[$i]->woeid;
              }
          }
      }
      else if($_GET['type'] == "zip"){
          if($xmlData->woeid){
              $woeids[0] = $xmlData->woeid;
          }
      }
      $weatherData = array();

      $newURL;
      if(count($woeids) > 0) {
        for($i = 0; $i < count($woeids); $i++){
          if($woeids[$i]){
              $newURL = $weatherURL . $woeids[$i];
  
              if($_GET['tempUnit'] == "c"){
                  $newURL = $newURL . $tempCStr;
              }
              else {
                  $newURL = $newURL . $tempFStr;
              }
              
              $headers = get_headers($newURL);
              $errors = substr($headers[0], 9, 3);
              
              if($errors != "404"){ // no errors occurred when opening the page	
                  $weatherStr = file_get_contents($newURL);
                  $weatherData[$i] = simplexml_load_string($weatherStr);
              }
          }
        } 
        
        $loc = $weatherData[0];
        $response = new SimpleXMLElement('<weather></weather>');
        $response->addChild('feed', $newURL);
        $location = $response->addChild('location');
        $units = $response->addChild('units');
        $condition = $response->addChild('condition');
        
        if($loc->channel->item->link){
          $channel = $loc->channel;
          $yweather_channel = $channel->children("http://xml.weather.yahoo.com/ns/rss/1.0"); 
          $yweather_item = $channel->item->children("http://xml.weather.yahoo.com/ns/rss/1.0"); 
          $geo_item = $channel->item->children("http://www.w3.org/2003/01/geo/wgs84_pos#");
            
          // Link
          if($channel->link){
            $response->addChild('link', ((string) $channel->link));
          }
          else { $response->addChild('link', ''); }
            
          // Image
          $matchStr = '/(http)(.+).gif/';
          preg_match($matchStr, $channel->item->description, $matches);
          $response->addChild('img', $matches[0]);

          // Condition -> Text
          if($yweather_item->condition && $yweather_item->condition->attributes()){ 
              $attr = $yweather_item->condition->attributes(); 
              if(isset($attr['text'])){
                  if($attr['text'] != ""){
                      $condition->addAttribute('text', $attr['text']);
                  }		
                  else { $condition->addAttribute('text', ''); } 
              }
              else { $condition->addAttribute('text', ''); }
          }
          else { $condition->addAttribute('text', ''); }
              
          // Condition - > Temperature
          if($yweather_item->condition && $yweather_item->condition->attributes()){ 
              $attr = $yweather_item->condition->attributes(); 
              if(isset($attr['temp'])){
                  if($attr['temp'] != ""){
                      $condition->addAttribute('temp', $attr['temp']);
                  }		
                  else { $condition->addAttribute('temp', ''); }
              }
              else { $condition->addAttribute('temp', ''); }
          }
          else { $condition->addAttribute('temp', ''); }
          
          // Units    
          if($yweather_channel->units && $yweather_channel->units->attributes()){ 
              $attr = $yweather_channel->units->attributes(); 
              if(isset($attr['temperature'])){
                  if($attr['temperature'] != ""){
                      $units->addAttribute('temperature', $attr['temperature']);
                  }		
                  else { $units->addAttribute('temperature', ''); } 
              }
              else { $units->addAttribute('temperature', ''); }
          }
          else { $units->addAttribute('temperature', ''); }
          
          // Location -> City
          if($yweather_channel->location && $yweather_channel->location->attributes()){ 
              $attr = $yweather_channel->location->attributes();
              if(isset($attr['city'])){
                  if($attr['city'] != ""){ 
                      $location->addAttribute('city', $attr['city']);
                  }		
                  else { $location->addAttribute('city', ''); } 
              }
              else { $location->addAttribute('city', ''); }
          }
          else { $location->addAttribute('city', ''); }
          
          // Location -> Region
          if($yweather_channel->location && $yweather_channel->location->attributes()){ 
              $attr = $yweather_channel->location->attributes();
              if(isset($attr['region'])){
                  if($attr['region'] != ""){ 
                      $location->addAttribute('region', $attr['region']);
                  }		
                  else { $location->addAttribute('region', ''); }
              }
              else { $location->addAttribute('region', ''); }
          }
          else { $location->addAttribute('region', ''); }
          
          // Location -> Country
          if($yweather_channel->location && $yweather_channel->location->attributes()){ 
              $attr = $yweather_channel->location->attributes(); 
              if(isset($attr['country'])){
                  if($attr['country'] != ""){
                      $location->addAttribute('country', $attr['country']);
                  }		
                  else { $location->addAttribute('country', ''); } 
              }
              else { $location->addAttribute('country', ''); }
          }
          else { $location->addAttribute('country', ''); }
          
          // Forecast
          foreach($yweather_item->forecast as $forecast) {
            if($forecast->attributes()){
                $forecastData = $response->addChild('forecast');
                $attr = $forecast->attributes(); 
                if(isset($attr['day'])){
                    if($attr['day'] != ""){
                        $forecastData->addAttribute('Day', $attr['day']);
                    }		
                    else { $forecastData->addAttribute('Day', ''); } 
                }
                else { $forecastData->addAttribute('Day', ''); }
                
                if(isset($attr['low'])){
                    if($attr['low'] != ""){
                        $forecastData->addAttribute('Low', $attr['low']);
                    }		
                    else { $forecastData->addAttribute('Low', ''); } 
                }
                else { $forecastData->addAttribute('Low', ''); }
                
                if(isset($attr['high'])){
                    if($attr['high'] != ""){
                        $forecastData->addAttribute('High', $attr['high']);
                    }		
                    else { $forecastData->addAttribute('High', ''); } 
                }
                else { $forecastData->addAttribute('High', ''); }
                
                if(isset($attr['text'])){
                    if($attr['text'] != ""){
                        $forecastData->addAttribute('Weather', $attr['text']);
                    }		
                    else { $forecastData->addAttribute('Weather', ''); } 
                }
                else { $forecastData->addAttribute('Weather', ''); }
            }
            else {
              $forecastData->addAttribute('Day', '');
              $forecastData->addAttribute('Low', '');
              $forecastData->addAttribute('High', '');
              $forecastData->addAttribute('Weather', '');
            }
          }
        }
        echo $response->asXML();
      }
      else { // no cities were found
        echo "No results found!";
      }
  }
  else { // invalid url given
    echo "No results found!";
  }
  
?>
