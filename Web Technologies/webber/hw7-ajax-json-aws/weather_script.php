<?php
  
  $geoURL = "http://where.yahooapis.com/v1/";
  $appIdStr = "?appid=EdponavV34HiU.GzWTw0LJR_imzTWhlOE3mUqwBoNKhHIcbujH6M2pVgsSGdIBP82b6mXDW.R6CgaYJgR.o.vbcEOlz0K9c-";
  $zipCodeStr = "concordance/usps/";
  $cityStr1 = "places\$and(.q('";
  $cityStr2 = "'),.type(7));start=0;count=5";

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
        $response->addChild('feed', 'url');
        $response->addChild('link', 'url');
        $location = $response->addChild('location');
        $units = $response->addChild('units');
        $condition = $response->addChild('condition');
        $forecasts = $response->addChild('forecast');
        $forecasts = array('1', '2', '3', '4', '5');
      
        
        if($loc->channel->item->link){
          $channel = $loc->channel;
          $yweather_channel = $channel->children("http://xml.weather.yahoo.com/ns/rss/1.0"); 
          $yweather_item = $channel->item->children("http://xml.weather.yahoo.com/ns/rss/1.0"); 
          $geo_item = $channel->item->children("http://www.w3.org/2003/01/geo/wgs84_pos#");
            
          // Weather
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
          foreach($forecasts as $forecast) {
            if($yweather_item->forecast && $yweather_item->forecast->attributes()){ 
                $attr = $yweather_item->forecast->attributes(); 
                if(isset($attr['day'])){
                    if($attr['day'] != ""){
                        $forecast->addAttribute('day', $attr['day']);
                    }		
                    else { $forecast->addAttribute('day', ''); } 
                }
                else { $forecast->addAttribute('day', ''); }
                
                if(isset($attr['low'])){
                    if($attr['low'] != ""){
                        $forecast->addAttribute('low', $attr['low']);
                    }		
                    else { $forecast->addAttribute('low', ''); } 
                }
                else { $forecast->addAttribute('low', ''); }
                
                if(isset($attr['high'])){
                    if($attr['high'] != ""){
                        $forecast->addAttribute('high', $attr['high']);
                    }		
                    else { $forecast->addAttribute('high', ''); } 
                }
                else { $forecast->addAttribute('high', ''); }
                
                if(isset($attr['text'])){
                    if($attr['text'] != ""){
                        $forecast->addAttribute('text', $attr['text']);
                    }		
                    else { $forecast->addAttribute('text', ''); } 
                }
                else { $forecast->addAttribute('text', ''); }
            }
            else {
              $forecast->addAttribute('day', '');
              $forecast->addAttribute('low', '');
              $forecast->addAttribute('high', '');
              $forecast->addAttribute('text', '');
            }
          }
        }
        echo $response;
      }
      else { // no cities were found
        
      }
  }
  else { // invalid url given
    
  }
  
?>
