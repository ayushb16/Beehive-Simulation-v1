import java.util.Scanner;
import java.util.Random;
import java.io.*;

public class BeehiveSimulation {
	//Global variables declared as public in case it will be used in the GUI program
	public static int[][] workerBee = new int[1000][6];
	public static int[] configValues = new int[3];
	public static int[] pollenCapacity = new int[3];
	public static int configWorkers = 0;;
	public static int totalEggsLaid, dailyEggsLaid = 0;
	public static double honeyStock = 0;
	public static int totalPollenCollected, pollenCollected = 0;
	public static int numberOfWorkerBees = 0;
	public static int numberOfLarva = 0;
	public static int numberOfPupa = 0;
	public static int numberOfDrones = 0;
	public static int numberOfDeaths = 0;
	public static int numberOfBirths = 0;
	public static int DayOfSimulation; 
	
	public static void readSimulationConfig() {
		//Read and store in an array configValues
		Scanner inputFileScanner = null;
		int noOfLines = 0;
		String line = "";
		String str = "";
		
		try {
			
			inputFileScanner = new Scanner(new FileInputStream("simconfig.txt"));
			while(inputFileScanner.hasNextLine() && noOfLines < 3) {
				
				//Read each line
				for(noOfLines = 0; noOfLines < 3; noOfLines++) {
					
				line = inputFileScanner.nextLine();
				
				for (int i = 0; i < line.length(); i++) {
					
					if(Character.isWhitespace(line.charAt(i))) {
						//Read the rest of the line starting from the space
						for (int j = (i + 1); j < line.length(); j++) {
							str += line.charAt(j);
						}
						configValues[noOfLines] = Integer.parseInt(str);
						//Reset str to avoid unnecessary carry forward of previous values
						str = "";
					} else {
						
					}
					
				}
			}
		}
			
		}catch (FileNotFoundException e) {
			// TODO: handle exception
			System.out.println("File not found!");
			System.out.println("Or File is not of specified format!");
			e.printStackTrace();
		}

	}
	public static void initBeesArray() {

		//Initialize all rows to 0
		for (int i = 0; i < workerBee.length; i++) {
				
			for (int j = 0; j < 6; j++) {
				workerBee[i][j] = 0;
			}
		}
		
		//Read the value given by simconfig.txt
		configWorkers = configValues[1];
		numberOfWorkerBees = configWorkers;
		if (configWorkers > 0) {
			//Add workers to workerBee
			for (int i = 0; i < configWorkers; i++) {
				
					workerBee[i][0] = i;//BeeID
					//BeeAge, assume just born
					workerBee[i][1] = 21;//Not 20, because metamorphose might conflict
					workerBee[i][2] = 4;//BeeType
			}
		}
	}
	public static int layDailyEggs() {
		Random randomEggs = new Random();
		dailyEggsLaid = 0;
		final int MAX_EGGS = 50;
		final int MIN_EGGS = 10;
		
		dailyEggsLaid = randomEggs.nextInt((MAX_EGGS - MIN_EGGS) + 1) + MIN_EGGS;
		return dailyEggsLaid;//To pass as argument to addEggToHive
}
	public static void addEggToHive(int eggs) {
		
			//Update the workerBee array with available slots
			//Traverse the 2D array and while beeId != 0, add eggs and update
			//Note: You can pass the method(layDailyEggs) as a parameter to this method.
			for(int i = 0; i < workerBee.length; i++) {
				
				if(i == 0) {
					//Always have a config file, otherwise, the first row will be skipped.
					//First ID of a bee due to 0-based indexing is conflicting with the if condition below
					continue;
				}
				if(workerBee[i][0] == 0) {
					//Available space!
					workerBee[i][0] = i;// ID
					workerBee[i][1] = 0;//Age = 0, for incrementAge to increment to 1 in day 1
					workerBee[i][2] = 1;//Type egg = 1
					eggs--;
				}
				if(eggs == 0) {
					break;
				}
			}

}
	
	public static void countTypesOfBees() {
		//Initialize for each day
		numberOfBirths = 0;
		totalEggsLaid = 0;
		numberOfLarva = 0;
		numberOfPupa = 0;
		numberOfWorkerBees = 0;
		numberOfDrones = 0;
		
		//To call upon updating type of each bee
		//Count Types of Bees
		for (int i = 0; i < workerBee.length; i++) {
			
			if(workerBee[i][2] == 1) {
				totalEggsLaid++;
			}else if(workerBee[i][2] == 2) {
				numberOfLarva++;
				
			}else if(workerBee[i][2] == 3) {
				numberOfPupa++;

			}else if(workerBee[i][2] == 4) {
				numberOfWorkerBees++;
				
			}else if(workerBee[i][2] == 5) {
				numberOfDrones++;
			}
		}	
		//As observed by screenshot provided on coursework!
		numberOfBirths = numberOfLarva + numberOfPupa;
}
	public static void funeral(int rowOfBee) {
		for(int j = 0; j < 6; j++) {
			//Set each column of specific row(i) to 0
			workerBee[rowOfBee][j] = 0;	
		}
		numberOfDeaths++;
}
	public static void incrementAge() {	
		for (int i = 0; i < workerBee.length; i++) {
			
			//Update everyday to be called in aDayPasses
			//Only increment of bees that EXIST!!
			//Or could have used 'Alive' attribute to check before incrementing age
			if(workerBee[i][2] == 1 || workerBee[i][2] == 2 || workerBee[i][2] == 3 ||
			   workerBee[i][2] == 4 || workerBee[i][2] == 5) {
				
				workerBee[i][1] += 1;
			}		
		}
}
	//Method called once daily
	public static void metamorphose() {		
		Random random = new Random();
		
		for (int i = 0; i < workerBee.length; i++) {
			
			//Updating the type of bee based on age of Bee
			if(workerBee[i][1] == 4) {
				workerBee[i][2] = 2;	

			}else if (workerBee[i][1] == 10) {	
				workerBee[i][2] = 3;

			}else if(workerBee[i][1] == 20){
				//Probability for a drone to emerge is 10%(As per area under curve, should be less than or equal to 10%)
				double probability = random.nextDouble();
				if (probability <= 0.1) {	
					workerBee[i][2] = 5;//Drone Bee

				}else{
					workerBee[i][2] = 4;//Worker Bee	
			}
		}
			if (workerBee[i][1] == 36 || workerBee[i][1] == 35 || workerBee[i][1] == 56) {
				//Call a funeral
				//36 for configValues workers
				funeral(i);
		}
	}
}
	public static void aDayPasses() {
	
		emptyStomachOfAllBees();
		resetFlowerArray();
		addEggToHive(layDailyEggs());
		incrementAge();
		metamorphose();
		AllWorkerBeesGardenSorties();
		feedingTime();
		undertakerCheck();
		printBeehiveStatus();
		logDailyStatusToFile(DayOfSimulation);

}
	public static void resetFlowerArray() {
		 pollenCapacity[0] = 20000;//Roses
		 pollenCapacity[1] = 50000;//Frangipani
		 pollenCapacity[2] = 10000;//Hibiscus
	}
	public static int visitFlower() {
		//This method is for one visit by 1 bee
		//First check whether it is a worker bee before calling this method
		//Related to method AllWorkerBeesGardenSorties() which is executed 5 times
		//The first time, all bees who are of workers' type: visitFlower() is called
		pollenCollected = 0;//If remains 0, implies no pollen
		String[] flowerTypes = {"Roses", "Frangipani", "Hibiscus"};
		//Randomly choose depending on the length and so, each one has an equal chance of being selected
		String choice = flowerTypes[new Random().nextInt(flowerTypes.length)];

		if(choice.equalsIgnoreCase("Roses")) {
			pollenCapacity[0] -= 20;
			pollenCollected = 20;
		}
		if(choice.equalsIgnoreCase("Frangipani")) {
			pollenCapacity[1] -= 50;
			pollenCollected = 50;
		}
		if(choice.equalsIgnoreCase("Hibiscus")) {
			pollenCapacity[2] -= 10;
			pollenCollected = 10;
		}
		return pollenCollected;
	}
	
	public static void printFlowerGarden() {
		System.out.print("Flower 1:Roses pollen stock: " + pollenCapacity[0] + 
						"\nFlower 2:Frangipani pollen stock: " + pollenCapacity[1] + 
						"\nFlower 3:Hibiscus pollen stock: " + pollenCapacity[2] + "\n");
	}
	//To be called at the end of the day
	public static void printBeehiveStatus() {
		//Called here to account for possible funerals before doing the count
		countTypesOfBees();
		System.out.print("Queen laid " + dailyEggsLaid + " eggs!" + 
						 "\nBeehive status\nEgg Count: "+ totalEggsLaid + "\nLarva Count: " + numberOfLarva + "\nPupa Count: " + numberOfPupa +
						 "\nWorker Count: "+ numberOfWorkerBees + "\nDrone Count: " + numberOfDrones + 
						 "\nDeath Count: " + numberOfDeaths + "\nBirth Count: "+ numberOfBirths +
						 "\nHoney Stock: " + honeyStock +"\n");
		printFlowerGarden();
	}
	
	public static void logDailyStatusToFile(int day) {
		String line = "";
		PrintWriter fileStream = null;
		try {
			fileStream = new PrintWriter(new FileOutputStream("simLog.csv", true));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("File not found!");
		}
		line = (day+1)+";"+dailyEggsLaid+";"+totalEggsLaid+";"+numberOfLarva+";"+numberOfPupa+";"+numberOfWorkerBees+";"
				+numberOfDrones+";"+numberOfDeaths+";"+numberOfBirths+";"+honeyStock+";"+pollenCapacity[0]+";"+pollenCapacity[1]
				+";"+pollenCapacity[2];
		fileStream.println(line);
		fileStream.close();
		
	}

	public static void logHeadingToFile() {
		//To be called once
		String heading = "";
		PrintWriter fileStream = null;
		try {
			fileStream = new PrintWriter(new FileOutputStream("simLog.csv"));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("File not found!");
		}
		heading = "Day;eggsLaid;eggInHive;Larva;Pupa;Worker;Drone;Death;Birth;HoneyStock;Flower1Pollen;Flower2Pollen;Flower3Pollen";
		fileStream.println(heading);
		fileStream.close();
	}
	public static void AllWorkerBeesGardenSorties() {
		totalPollenCollected = 0;
		int count = 0;
		//5 total visits
		for (int i = 0; i < 5; i++) {
			for(int j = 0; j < workerBee.length; j++) {

				if (workerBee[j][1] >= 20 && workerBee[j][2] == 4) {//** j acts as the i ie the row
					//Only worker bees are allowed to visit the garden
					int pollen = visitFlower();
					if(pollen == 0) {
						
						while(pollen == 0 && count <= 5) {
							//Allow another flower type to be visited
							pollen = visitFlower();
							count++;
						}
					}
				//Update Pollen Collection Sorties in workerBee array for each bee
				workerBee[j][3] += pollenCollected;
				//Update total pollen collection
				totalPollenCollected += pollenCollected;
				}
			}
		}
	}
	public static void emptyStomachOfAllBees() {
		for(int i = 0; i < workerBee.length; i ++) {
			//Reset Eaten to 0 for all bees once at start of the day 
			workerBee[i][4] = 0;
		}
	}
	
	public static void feedingTime() {
		//Get total honey at start
		//To load honey from file only one time
		//Initialize to avoid carry forward(Accumulation) of previous honey
		if(DayOfSimulation == 0) {
			honeyStock = configValues[2];
		}
		
		int pollenToHoney = 0;
		pollenToHoney = (int) (totalPollenCollected / 40);
		honeyStock += pollenToHoney;
		
			if (honeyStock < 0) {
				System.out.print("Queen Bee did not have her quota of honey or there is not enough honey for the colony");
				System.exit(-1);
				
			} else {
				honeyStock -= 2;//For the Queen
			}
			
		//Feed each bee and update info in the workerBee array in hierarchical order(Larva, workers, drones)
		//LARVA
		for(int i = 0; i < workerBee.length; i++) {
				//Decrease honey stock and update Eaten value for each bee
			if(workerBee[i][2] == 2) {
				//Anomaly because of 0.5 being cast to int as it is float
				honeyStock -= 0.5;
				workerBee[i][4] = 1;//Can cause anomaly and increase death if 0.5 is not set
				//Let Eaten be any value, but not 0 which will indicate that it ate.(atleast)	
			}
		}
		//WORKERS
		for(int i = 0; i < workerBee.length; i++) {
			if(workerBee[i][2] == 4) {
				honeyStock -= 1;
				workerBee[i][4] = 1;
			}
		}
		//DRONES
		for(int i = 0; i < workerBee.length; i++) {
			if(workerBee[i][2] == 5) {
				honeyStock -= 1;
				workerBee[i][4] = 1;
			}
		}
	}
	
	public static void undertakerCheck() {
		//To be called at the end of each day
		for(int i = 0; i < workerBee.length; i++) {
			
			if( (workerBee[i][2] == 2 && workerBee[i][4] == 0) ) {
				funeral(i);
			}
			if( (workerBee[i][2] == 4 && workerBee[i][4] == 0) ) {
				funeral(i);
			}
			if( (workerBee[i][2] == 5 && workerBee[i][4] == 0) ) {
				funeral(i);
			}
	}
}
	public static void printWorkerBeeArray() {
		
		for (int i = 0; i < workerBee.length; i++) {
			
		for (int j = 0; j < 6; j++) {
			System.out.print(workerBee[i][j] + " ");
		}
		System.out.println();
	}
	}
	public static void inputConfigValues() {
		String line = "";
		PrintWriter fileStream = null;
		Scanner stdInputScanner = new Scanner(System.in);
		
		do {
			System.out.println("simulationDays: ");
			configValues[0] = stdInputScanner.nextInt();
			
			if (configValues[0] <= 0 || configValues[0] > 500) {
				System.out.println("Cannot input 0 or a negative value for days. Maximum simulation days: 500");
			}
		}while(configValues[0] <= 0 || configValues[0] > 500);
		do {
			System.out.println("initWorkers: ");
			configValues[1] = stdInputScanner.nextInt();
			
			if (configValues[1] <= 0 || configValues[1] > 250) {
				System.out.println("Cannot admit too many workers or 0 or negative value. Maximum worker bees: 250");
			}
		} while (configValues[1] <= 0 ||configValues[1] > 250 );

		do {	
			System.out.println("initHoney: ");
			configValues[2] = stdInputScanner.nextInt();
			
			if (configValues[2] <= 0 || configValues[2] > 20000) {
				System.out.println("Cannot input 0 or a negative value for honey. Maximum honey: 20000");
			}
			
		} while (configValues[2] <= 0 || configValues[2] > 20000);

		stdInputScanner.close();
		//The reason behind 3 while loops is to ensure that the values are correctly entered 1 after the other.
		
		//Write to file
		try {
			fileStream = new PrintWriter(new FileOutputStream("simconfig.txt"));
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("File not found!");
		}
		line =  "simulationDays " + configValues[0] + 
				"\ninitWorkers " + configValues[1] +
				"\ninitHoney " + configValues[2];
		fileStream.println(line);
		fileStream.close();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//readSimulationConfig();
		inputConfigValues();
		initBeesArray();
		logHeadingToFile();
		
		for (DayOfSimulation = 0; DayOfSimulation < configValues[0]; DayOfSimulation++) {
			System.out.println("*** This is day "+ (DayOfSimulation + 1) +" ***");
			aDayPasses();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			System.out.println();
		}
		System.out.println();
}

}
