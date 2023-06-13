package com.websocketdemo.game.model;

import java.util.LinkedList;

public class ServerTime {
	public static long serverTimeSync;
	public static long LocalTimeSync;
	private static LinkedList<Float> Deltas = new LinkedList<Float>();
	

	public static void Wipe(){
		Deltas.clear();
	}
	
	public static void AddDelta(float delta){
		/*Sliding Window Size of 6*/
		if (Deltas.size()>6){
			if((delta<(getMean()+standardDeviation()))&&(delta>(getMean()-standardDeviation()))){
				Deltas.removeFirst();
				Deltas.addLast(delta);
			}
		}else{
			Deltas.addLast(delta);
		}
	}
	
	private static float getMean(){
		float total=0;
		int counter=0;
		for (float i : Deltas){
			counter=0;
			total+=i;
			counter++;
		}
		if(counter>0){
		return (float)total/(float)counter;
		}
		return 0.001f;
	}
	
	private static float standardDeviation () {
		float total=0;
		float DeltasMean = getMean();
		for (float i: Deltas){
			total+=(Math.pow(i-DeltasMean, 2));
		}
		
		return (float)Math.sqrt(total/Deltas.size()-1);
	}
	

	public static float getMeanDelta(){
		return getMean();
		
	}
}
