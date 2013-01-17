#!/usr/bin/php
<?php
//File to generate batch of map file. didnt want to change orig python file. Just quick and dirty generator
//for setting of parameters (e.g. min/max number of ships per planet), go to python mapGenerator file
//This file generates 1 map with random size planets, with random locations.
$numMapsPerSize = 10;
$minPlanets = 5; 
$maxPlanets = 7;


$mapGenerator = "./mapGenerator.py";
$mapFolder = "maps/";




if (file_exists($mapFolder)) {
	`rm -r $mapFolder`;
	
}
mkdir($mapFolder);

while(!areWeDone()) {
	saveAndGenerateMap();
}


function areWeDone() {
	$done = true;
	global $minPlanets, $maxPlanets, $numMapsPerSize, $mapFolder;
	for ($i = $minPlanets; $i <= $maxPlanets; $i++) {
		$dir = $mapFolder.$i."planets/";
		$count = count(glob($dir . "*"));
		if ($count < $numMapsPerSize) {
			//not done yet. return false;
			$done = false;
			break;
		}
	}
	return $done;
}

function saveAndGenerateMap() {
	global $mapGenerator, $minPlanets, $maxPlanets, $mapFolder, $minPlanets, $maxPlanets, $numMapsPerSize;
	$mapString = shell_exec($mapGenerator." ".$minPlanets." ".$maxPlanets);
	$numPlanets = count(explode("\n", $mapString)) - 1;
	
	if ($numPlanets >= $minPlanets && $numPlanets <= $maxPlanets) {
		$destMap = $mapFolder.$numPlanets."planets/";
		if (!file_exists($destMap)) {
			mkdir($destMap);
		}
		if (count(glob($destMap . "*")) >= $numMapsPerSize) {
			//we have enough maps for this planet size. ignore this mapstring
		} else {
			file_put_contents(getMapFilePath($destMap), $mapString);
		}
	} else {
		//the generate script might deviate slightly from the min/max planets parameter
		//just ignore this one
	}

}

function getMapFilePath($destMap) {
	$count = count(glob($destMap . "*")) + 1;
	return $destMap. "map".$count;
}