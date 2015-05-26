package com.codey.OpenGL;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import static org.apache.commons.math3.util.FastMath.toDegrees;

public class SimulationCreator {

	private static final String SIMULATION_FILE_PATH = "/home/cody/School/SeniorProject/Simulations/newGalaxy.sim";
	
	private static final double SOLAR_SYSTEM_WIDTH = 1920;
	private static final double SOLAR_SYSTEM_HEIGHT = 1080;

	private static final Vector3D MILKYWAY_POSITION = new Vector3D(0, 0, 0);
	private static final Vector3D MILKYWAY_VELOCITY = new Vector3D(0, 0, 0);
	private static final double MILKYWAY_MASS = 100;
	private static final double MILKYWAY_RADIUS = 5;
	private static final double MILKYWAY_DISK_RADIUS = 50;
	private static final double[] BLACKHOLE_COLOR = {.1,.1,.1};

	private static final Vector3D ANDROMEDA_POSITION = new Vector3D(-200, 130, 0);
	private static final Vector3D ANDROMEDA_VELOCITY = new Vector3D(40, -10, 0);
	//private static final Vector3D ANDROMEDA_POSITION = new Vector3D(-75, 55, 0);
	//private static final Vector3D ANDROMEDA_VELOCITY = new Vector3D(37, 0, 0);
	private static final double ANDROMEDA_MASS = 100;
	private static final double ANDROMEDA_RADIUS = 5;
	private static final double ANDROMEDA_DISK_RADIUS = 50;
	
	private static final int NUM_STARS = 12;
	private static final int NUM_RINGS = 6;
	
	
	private double gravitationalConstant;
	private double conservationTolerance;
	private double barnesHutTheta;
	private double collisionThreshold;
	private double timeStep;
	
	private int maxStep;
	private int particleToTrack;
	private int recurringSave;
	
	private boolean showOctTree;
	private boolean breakOnEnergyNotConserved;
	private boolean graphicsEnabled;
	private boolean saveScreenshots;

	private String conservationFileName;
	private String simulationFileName;
	private String performanceFileName;
	private String energyFileName;
	private boolean zipFiles;
	private String savedFilePath;

	private Librarian linda;
	
	public ArrayList<Particle> particles;
	
	public SimulationCreator() throws IOException {
		
		setOptions();
		
		particles = new ArrayList<Particle>();
		
		uniformMilkyWay();
		uniformAndromeda();
		
		//randomMilkyWay();
		//randomAndromeda();
		
		linda = new Librarian(particles, null);
		
		linda.setSimulationFileName(SIMULATION_FILE_PATH);
		savedFilePath = linda.recordSimulation(optionsString());
		linda.janitor();
	}
	
	public String getSavedFilePath() {
		return savedFilePath;
	}
	
	private void setOptions() {
		gravitationalConstant = 449.93;
		conservationTolerance = 0;
		barnesHutTheta = 0;
		collisionThreshold = 0;
		timeStep = 0.01;
		maxStep = 900;
		recurringSave = 0;
		particleToTrack = 0;
		showOctTree = false;
		breakOnEnergyNotConserved = false;
		graphicsEnabled = true;
		saveScreenshots = true;
		
		conservationFileName = "/home/cody/School/SeniorProject/Output/Conservation.csv";
		performanceFileName = "none"; //"/home/cody/School/SeniorProject/Output/Performance.csv";
		simulationFileName = "none"; //"/home/cody/School/SeniorProject/Output/Simulation.sim";
		energyFileName = "/home/cody/School/SeniorProject/Output/Energy.csv";
		
		zipFiles = false;
		
		savedFilePath = null;
		
	}
	
	private void uniformMilkyWay() {
		Particle blackHole;
		Particle star;
		Vector3D position;
		Vector3D velocity;
		double radius;
		int currentStar;
		int starsPerRing;
		double theta;
		double enclosedMass;
		
		blackHole = new Particle(MILKYWAY_POSITION, MILKYWAY_VELOCITY, MILKYWAY_MASS, MILKYWAY_RADIUS);
		particles.add(blackHole);
		enclosedMass = MILKYWAY_MASS;
		
		currentStar = 0;
		starsPerRing = NUM_STARS / NUM_RINGS;
		
		for (int ring = 0; ring < NUM_RINGS; ring++) {
			radius = (ring + 1) * MILKYWAY_DISK_RADIUS / NUM_RINGS;

			enclosedMass += starsPerRing;
			for (double i = 0; i < starsPerRing && currentStar < NUM_STARS; i++, currentStar++) {
				theta = (2 * Math.PI) * (i / (double)starsPerRing);
				position = pointOnCircle(radius, theta, 0);
				velocity = velocityVector(position, enclosedMass);
				
				position = position.add(MILKYWAY_POSITION);
				velocity = velocity.add(MILKYWAY_VELOCITY);
				
				star = new Particle(position, velocity, 1, 1);
				particles.add(star);
			}
			
		}
	}
	
	private void uniformAndromeda() {
		Particle blackHole;
		Particle star;
		Vector3D position;
		Vector3D velocity;
		double radius;
		int currentStar;
		int starsPerRing;
		double theta;
		double enclosedMass;
		
		blackHole = new Particle(ANDROMEDA_POSITION, ANDROMEDA_VELOCITY, ANDROMEDA_MASS, ANDROMEDA_RADIUS);
		particles.add(blackHole);
		enclosedMass = ANDROMEDA_MASS;
		
		currentStar = 0;
		starsPerRing = NUM_STARS / NUM_RINGS;
		
		for (int ring = 0; ring < NUM_RINGS; ring++) {
			radius = (ring + 1) * ANDROMEDA_DISK_RADIUS / NUM_RINGS;
			
			for (double i = 0; i < starsPerRing && currentStar < NUM_STARS; i++, currentStar++) {
				theta = (2 * Math.PI) * (i / (double)starsPerRing);
				position = pointOnCircle(radius, theta, 0);
				velocity = velocityVector(position, enclosedMass).scalarMultiply(-1);
				
				position = position.add(ANDROMEDA_POSITION);
				velocity = velocity.add(ANDROMEDA_VELOCITY);
				
				star = new Particle(position, velocity, 1, 1);
				particles.add(star);
			}
			
			enclosedMass += starsPerRing;
		}
	}
	
	private void randomMilkyWay() {
		Particle blackHole;
		Particle star;
		Vector3D position;
		Vector3D velocity;
		double radius;
		Random random = new Random();
		
		blackHole = new Particle(MILKYWAY_POSITION, MILKYWAY_VELOCITY, MILKYWAY_MASS, MILKYWAY_RADIUS);
		particles.add(blackHole);
		System.out.print(blackHole);
		
		for (int i = 0; i < NUM_STARS; i++) {
			radius = (random.nextDouble() * MILKYWAY_DISK_RADIUS);
			position = randomPointOnCircle(radius);
			velocity = velocityVector(position, Math.sqrt(gravitationalConstant * MILKYWAY_MASS / radius));
			
			position = position.add(MILKYWAY_POSITION);
			velocity = velocity.add(MILKYWAY_VELOCITY);
			
			star = new Particle(position, velocity, 1, 1);
			particles.add(star);
			System.out.print(star);
		}
	}
	
	private void randomAndromeda() {
		Particle blackHole;
		Particle star;
		Vector3D position;
		Vector3D velocity;
		double radius;
		Random random = new Random();
		
		blackHole = new Particle(ANDROMEDA_POSITION, ANDROMEDA_VELOCITY, ANDROMEDA_MASS, ANDROMEDA_RADIUS);
		particles.add(blackHole);
		System.out.print(blackHole);

		for (int i = 0; i < NUM_STARS; i++) {
			radius = (random.nextDouble() * ANDROMEDA_DISK_RADIUS);
			position = randomPointOnCircle(radius);
			velocity = velocityVector(position, Math.sqrt(gravitationalConstant * ANDROMEDA_MASS / radius));
			
			position = position.add(ANDROMEDA_POSITION);
			velocity = velocity.add(ANDROMEDA_VELOCITY);
			
			star = new Particle(position, velocity, 1, 1);
			particles.add(star);
			System.out.print(star);
		}
	}
	
	private Vector3D pointOnCircle(double radius, double theta, double zTilt) {
		double x, y, z;
		
		x = radius * Math.cos(theta);
		y = radius * Math.sin(theta);
		z = y * Math.sin(zTilt);
		
		return new Vector3D(x, y, z);
	}
	
	private Vector3D randomPointOnCircle(double radius) {
		Random randomGenerator;
		double theta, x, y, z;
		
		randomGenerator = new Random();
		
		theta = 2 * Math.PI * randomGenerator.nextDouble();
		x = radius * Math.cos(theta);
		y = radius * Math.sin(theta);
		z = y * Math.cos(Math.PI / 4);
		
		return new Vector3D(x, y, z);
	}
	
	private Vector3D velocityVector(Vector3D position, double enclosedMass) {
		Vector3D v;
		double theta;
		double velocity;
		
		velocity = Math.sqrt(gravitationalConstant * enclosedMass / position.getNorm());
		
		theta = Math.atan2(position.getY(), position.getX()) + (Math.PI / 2.0);
		
		v = new Vector3D(Math.cos(theta), Math.sin(theta), 0);
		return v.scalarMultiply(velocity);
	}

	/*----------------------------------------------------------------------
	 *  File Writing Methods
	 *---------------------------------------------------------------------*/
	 
	private String optionsString() {
		String options = "";

		options += String.format("Gravitational_Constant:  %.16f\n", gravitationalConstant);
		options += String.format("Conservation_Tolerance:  %.16f\n", conservationTolerance);
		options += String.format("Collision_Threshold:     %.16f\n", collisionThreshold);
		options += String.format("Barnes_Hut_Theta:        %.16f\n", barnesHutTheta);
		options += String.format("Time_Step:               %.16f\n", timeStep);
		options += String.format("Max_Step:                %d\n", maxStep);
		options += String.format("Particle_To_Track:       %d\n", particleToTrack);
		options += String.format("Recurring_Save:          %d\n", recurringSave);
		options += "Show_Oct_Tree:           " + showOctTree + "\n";
		options += "Strict_Conservation:     " + breakOnEnergyNotConserved + "\n";
		options += "Graphics_Enabled:        " + graphicsEnabled + "\n";
		options += "Save_Screenshots:        " + saveScreenshots + "\n";
		options += "\n";
		options += "Conservation_File_Path:  " + conservationFileName + "\n";
		options += "Performance_File_Path:   " + performanceFileName + "\n";
		options += "Simulation_File_Path:    " + simulationFileName + "\n";
		options += "Energy_File_Path:        " + energyFileName + "\n";
		options += "\n";
		
		return options;
	}
}