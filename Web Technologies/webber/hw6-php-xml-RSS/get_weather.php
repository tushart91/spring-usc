<html>
		<head>
				<meta charset="utf-8">
        <title>HW6</title>
    
        <script language="javascript">
            function validate(){ 
                if((document.forms["searchForm"]["location"].value=="") || (document.forms["searchForm"]["location"].value==null)){ // check to make sure a location was entered
                    window.alert("Please enter a location.");
										return false;
                }
								if (document.getElementById('dropdown').selectedIndex == 0) { // the location is a city
                    var re = /^[a-zA-Z]+(?:[\s-][a-zA-Z]+)*$/;
                    var match = re.exec(document.forms["searchForm"]["location"].value);
                    if (!match) {
                        window.alert("If searching for a city, ensure the city name contains only letters and single spaces.");
												return false;
                    }
                }
                if (document.getElementById('dropdown').selectedIndex == 1) { // the location is a zip code
                    var re = /^(?:\d{5})$/;
                    var match = re.exec(document.forms["searchForm"]["location"].value);
                    if (!match) {
                        window.alert("If searching for a zip code, ensure the zip code is composed of 5 numbers.");
												return false;
                    }
                }

								var radios = document.forms["searchForm"]["temperature"];
								var valid = false;
								for(var i = 0; i < radios.length; i++){
										if(radios[i].checked){
												valid = true;
												break;
										}
								}
								if(!valid){ // neither of the temperature radio buttons was selected
										window.alert("Please choose a temperature unit.");
										return false;
								}
								return true;
            }
        </script>
				
        <style type="text/css">
            .searchForm {
                height: 100px;
                width: 400px;
                border: 4px double black;
                margin: 0 auto;
                padding: 20px;
            }

            .input-rounded-button {
                -webkit-border-radius: 3px;
                -moz-border-radius: 3px;
                border-radius: 3px;
                border: 1px solid gray;
                font-size: 12px;
                cursor: pointer;
            }
        </style>
    </head>

    <body>
        <div style="text-align: center;"><h3>Weather Search</h3></div>

        <div class="searchForm">
            <form name="searchForm" onsubmit="return validate()" method="POST" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>">
                Location: <input type="text" name="location" value="<?php if ($_SERVER["REQUEST_METHOD"] == "POST") {echo $_POST['location']; } ?>" style="width: 230px; margin-left: 100px; margin-bottom: 10px;"><br>
                Location Type: <select name="locationType" id="dropdown" style="margin: 0px 0px 10px 64px;">
                    <option value="city" <?php if ($_SERVER["REQUEST_METHOD"] == "POST"){ if($_POST['locationType'] == "city"){ echo "selected"; } } ?> selected>City</option>
                    <option value="zip" <?php if ($_SERVER["REQUEST_METHOD"] == "POST"){ if($_POST['locationType'] == "zip"){ echo "selected"; } } ?> >Zip Code</option>
                </select><br>
                Temperature Unit: <input type="radio" name="temperature" value="fahrenheit" style="margin-left: 45px;"
																  <?php if ($_SERVER["REQUEST_METHOD"] == "POST"){ if($_POST['temperature'] == "fahrenheit"){ echo "checked"; } } ?> >Fahrenheit
                                  <input type="radio" name="temperature" value="celsius" style="margin-left: 30px;"
																	<?php if ($_SERVER["REQUEST_METHOD"] == "POST"){ if($_POST['temperature'] == "celsius"){ echo "checked"; } } ?> >Celsius
                <br><div style="text-align: center; margin-top: 10px;"><input class="input-rounded-button" type="submit" name="submit" value="Search"></div>
            </form>
        </div>
    </body>

</html>

<?php
    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        
        $geoURL = "http://where.yahooapis.com/v1/";
        $appIdStr = "?appid=EdponavV34HiU.GzWTw0LJR_imzTWhlOE3mUqwBoNKhHIcbujH6M2pVgsSGdIBP82b6mXDW.R6CgaYJgR.o.vbcEOlz0K9c-";
        $zipCodeStr = "concordance/usps/";
        $cityStr1 = "places\$and(.q('";
        $cityStr2 = "'),.type(7));start=0;count=5";

        $weatherURL = "http://weather.yahooapis.com/forecastrss?w=";
        $tempFStr = "&u=f";
        $tempCStr = "&u=c";

        if($_POST['locationType'] == "city"){
						// replace any spaces in a city name with + to make a url
						$cityName = $_POST['location'];
            $cityName = str_replace(' ', '+', $cityName);
            
            $geoURL = $geoURL . $cityStr1 . $cityName . $cityStr2 . $appIdStr;
        }
        else if($_POST['locationType'] == "zip"){
            $geoURL = $geoURL . $zipCodeStr . $_POST['location'] . $appIdStr;
        }

				$headers = get_headers($geoURL);
				$errors = substr($headers[0], 9, 3);
				
				if($errors != "404"){ // no errors occurred when opening the page	
						$xmlStr = file_get_contents($geoURL);
						$xmlData = simplexml_load_string($xmlStr);
		
						$woeids = array();
						if($_POST['locationType'] == "city"){
								for($i = 0; $i < count($xmlData); $i++){
										if($xmlData->place[$i]->woeid){
											 $woeids[$i] = $xmlData->place[$i]->woeid;
										}
								}
						}
						else if($_POST['locationType'] == "zip"){
								if($xmlData->woeid){
										$woeids[0] = $xmlData->woeid;
								}
						}
						$weatherData = array();
		
						if(count($woeids) > 0) {
								for($i = 0; $i < count($woeids); $i++){
										if($woeids[$i]){
												$newURL = $weatherURL . $woeids[$i];
						
												if($_POST['temperature'] == "celsius"){
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
								} ?>
								<br> <br> <div style="text-align:center" >
								<?php
										$count = 0;
										foreach($weatherData as $location) { 
												if($location->channel->item->link){
														$count++;
												}
										}
										echo $count; echo " result(s) for ";
										if($_POST['locationType'] == "city"){
												echo "City ";
										}
										else if($_POST['locationType'] == "zip"){
												echo "Zip Code ";
										}
										echo $_POST['location'];
								?> </div><br>
								
								<table border="1" style="margin: 0 auto; text-align:center; vertical-align:middle;">
										<?php foreach($weatherData as $location) { 
												if($location->channel->item->link){
														$channel = $location->channel;
														//echo $channel->item->description;
														$yweather_channel = $channel->children("http://xml.weather.yahoo.com/ns/rss/1.0"); 
														$yweather_item = $channel->item->children("http://xml.weather.yahoo.com/ns/rss/1.0"); 
														$geo_item = $channel->item->children("http://www.w3.org/2003/01/geo/wgs84_pos#"); ?>
						
														<tr><th width="100px">Weather</th><th width="100px">Temperature</th><th width="200px">City</th><th width="100px">Region</th>
														<th width="100px">Country</th><th width="100px">Latitude</th><th width="100px">Longitude</th><th width="100px">Link to Details</th></tr>
														<tr>
														<td> <!-- Weather -->
																<?php
																$matchStr = '/(http)(.+).gif/';
																preg_match($matchStr, $channel->item->description, $matches);
																echo "<a href=\"";
																echo $channel->item->link;
																echo "\" >";
																echo "<img src=\"";
																echo $matches[0];
																echo "\" alt=\"";
																if($yweather_item->condition && $yweather_item->condition->attributes()){ 
																		$attr = $yweather_item->condition->attributes(); 
																		if(isset($attr['text'])){
																				if($attr['text'] != ""){ 
																						echo $attr['text'];
																						echo "\" title=\"";
																						echo $attr['text'];
																				}		
																				else { echo("n/a"); } 
																		}
																		else { echo("n/a"); }
																}
																else { echo("n/a"); }
																echo "\" ></a>";
																?>
														</td>
														
														<td> <!-- Temperature -->
																<?php 
																if($yweather_item->condition && $yweather_item->condition->attributes()){ 
																		$attr = $yweather_item->condition->attributes(); 
																		if(isset($attr['text'])){
																				if($attr['text'] != ""){ 
																						echo $attr['text']; 
																				}		
																				else { echo("n/a"); } 
																		}
																		else { echo("n/a"); }
																}
																else { echo("n/a"); } ?>
																
																<?php 
																if($yweather_item->condition && $yweather_item->condition->attributes()){ 
																		$attr = $yweather_item->condition->attributes(); 
																		if(isset($attr['temp'])){
																				if($attr['temp'] != ""){ 
																						echo $attr['temp']; 
																				}		
																				else { echo("n/a"); }
																		}
																		else { echo("n/a"); }
																}
																else { echo("n/a"); } ?>&deg;
																
																<?php 
																if($yweather_channel->units && $yweather_channel->units->attributes()){ 
																		$attr = $yweather_channel->units->attributes(); 
																		if(isset($attr['temperature'])){
																				if($attr['temperature'] != ""){ 
																						echo $attr['temperature']; 
																				}		
																				else { echo("n/a"); } 
																		}
																		else { echo("n/a"); }
																}
																else { echo("n/a"); } ?>
														</td>
														<td> <!-- City -->
																<?php 
																if($yweather_channel->location && $yweather_channel->location->attributes()){ 
																		$attr = $yweather_channel->location->attributes();
																		if(isset($attr['city'])){
																				if($attr['city'] != ""){ 
																						echo $attr['city']; 
																				}		
																				else { echo("n/a"); } 
																		}
																		else { echo("n/a"); }
																}
																else { echo("n/a"); } ?>
														</td>
														<td> <!-- Region -->
																<?php 
																if($yweather_channel->location && $yweather_channel->location->attributes()){ 
																		$attr = $yweather_channel->location->attributes();
																		if(isset($attr['region'])){
																				if($attr['region'] != ""){ 
																						echo $attr['region']; 
																				}		
																				else { echo("n/a"); }
																		}
																		else { echo("n/a"); }
																}
																else { echo("n/a"); } ?>
														</td>
														<td> <!-- Country -->
																<?php 
																if($yweather_channel->location && $yweather_channel->location->attributes()){ 
																		$attr = $yweather_channel->location->attributes(); 
																		if(isset($attr['country'])){
																				if($attr['country'] != ""){ 
																						echo $attr['country']; 
																				}		
																				else { echo("n/a"); } 
																		}
																		else { echo("n/a"); }
																}
																else { echo("n/a"); } ?>
														</td>
														<td> <!-- Latitude -->
																<?php 
																if($geo_item->lat){
																		if($geo_item->lat != ""){ 
																				echo $geo_item->lat; 
																		}		
																		else { echo("n/a"); } 
																}
																else { echo("n/a"); } ?>
														</td>
														<td> <!-- Longitude -->
																<?php 
																if($geo_item->long){ 
																		if($geo_item->long != ""){ 
																				echo $geo_item->long; 
																		}			
																		else { echo("n/a"); } 
																}
																else { echo("n/a"); } ?>
														</td>
														<td> <!-- Link Details -->
																<a href="<?php 
																if($channel->link){ 
																		if($channel->link != ""){ 
																				echo $channel->link; 
																		}		
																		else { echo("n/a"); } 
																}
																else { echo("n/a"); } ?>
																">Details</a>
														</td>
														</tr>
												<?php }
										} ?>
								</table><br><br>
						<?php }
						else { // no cities were found
								echo("<br><br><div style=\"text-align:center;\"><h3>Zero results found!</h3></div>");	
						}
				}
				else { // invalid url given
						echo("<br><br><div style=\"text-align:center;\"><h3>Zero results found!</h3></div>");
				}
		} ?>
