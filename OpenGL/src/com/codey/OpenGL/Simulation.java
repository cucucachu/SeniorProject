package com.codey.OpenGL;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.lwjgl.LWJGLException;

public class Simulation {

	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	public static final int FRAME_RATE = 32;
	
	private Painter picaso;
	private CameraMan carl;
	private Conservationist colin;
	private Librarian linda;
	
	public ArrayList<Particle> particles;
	private Vector3D stars[];
	public SSGravitationalForce gravity;
	private static int step = 0;
	
	private double gravitationalConstant;
	private double conservationTolerance;
	private double barnesHutTheta;
	private double timeStep;
	private int maxStep;
	private boolean showOctTree;
	private boolean breakOnEnergyNotConserved;
	private boolean outputConservationInfo;
	
	public Simulation(File inputFile) throws Exception {
		particles = new ArrayList<Particle>();
		
		readInputFile(inputFile);
		
		initialize();
		
		simulate();
		
		cleanUp();
	}
	
	private void initialize() throws LWJGLException, FileNotFoundException, UnsupportedEncodingException {
		picaso = new Painter(WIDTH, HEIGHT, FRAME_RATE);
		carl = new CameraMan();
		colin = new Conservationist(particles, conservationTolerance, gravitationalConstant);
		linda = new Librarian(particles, colin);
		gravity = new SSGravitationalForce(gravitationalConstant, particles, barnesHutTheta);
		carl.setParticles(particles);
	}
	
	private void simulate() throws Exception {	
		Vector3D forces[];
		int i;
		boolean nonConservative;
		
		nonConservative = false;
		
		System.out.println("Step, Energy, Linear Momentum, Angular Momentum");
		
		while(!picaso.isCloseRequested() && step < maxStep) {
			
			picaso.checkForDisplayResize();
			picaso.clear();
			
			carl.setup();
			
			linda.recordConservation(step);
			linda.recordPerformance(step);
			
			forces = new Vector3D[particles.size()];
			
			i = 0;
			for (Particle particle : particles) {
				forces[i] = gravity.forceOnParticle(particle);
				i++;
			}
			
			i = 0;
			for (Particle particle : particles) {
				particle.step(timeStep, forces[i]);
				picaso.drawParticle(particle);
				i++;
			}
			
			gravity.updateOctTree();
			
			if (showOctTree)
				picaso.drawOctTree(gravity.getOctTree().getRoot());
			
			picaso.render();
			
			
			if (!colin.energyConserved()) {
				System.out.println("Energy Not Conserved!");
				nonConservative = true;
			}
			
			if (!colin.linearMomentumConserved()) {
				System.out.println("Linear Momentum Not Conserved!");
				nonConservative = true;
			}
			
			if (!colin.angularMomentumConserved()) {
				System.out.println("Angular Momentum Not Conserved!");
				nonConservative = true;
			}
			
			if (nonConservative && breakOnEnergyNotConserved)
				break;
			
			step++;
		}

	}
	
	private void cleanUp() throws IOException {
		linda.recordSimulation(optionsString());
		picaso.janitor();
		linda.janitor();
	}
	
	private void readInputFile(File inputFile) throws SimulationException, FileNotFoundException {
		Scanner scanner;
		String nextLine;
		
		System.out.println("Reading input...");
		
		scanner = new Scanner(inputFile);
		
		readParameters(scanner);
		
		if (scanner.next().toLowerCase().compareTo("particles:") != 0)
			throw new SimulationException("No particles given.");
		
		while (scanner.hasNextLine()) {
			nextLine = scanner.nextLine();
			if (!nextLine.trim().isEmpty())
				readParticle(nextLine);
		}

		printInput();
		
	}
	
	private void readParameters(Scanner scanner) throws SimulationException {
		String input;
		
		try {
			
			if (!scanner.hasNext())
				throw new SimulationException("Input file is empty");
			
			input = scanner.next();

			if (input.compareTo("Gravitational_Constant:") != 0)
				throw new SimulationException("Could not locate string Gravitational_Constant:");
			
			gravitationalConstant = new Double(scanner.next());

			input = scanner.next();
			if (input.compareTo("Conservation_Tolerance:") != 0)
				throw new SimulationException("Could not locate string Conservation_Tolerance:");
			
			conservationTolerance = new Double(scanner.next());

			input = scanner.next();
			if (input.compareTo("Barnes_Hut_Theta:") != 0)
				throw new SimulationException("Could not locate string Barnes_Hut_Theta:");
			
			barnesHutTheta = new Double(scanner.next());

			input = scanner.next();
			if (input.compareTo("Time_Step:") != 0)
				throw new SimulationException("Could not locate string Time_Step:");
			
			timeStep = new Double(scanner.next());

			input = scanner.next();
			if (input.compareTo("Max_Step:") != 0)
				throw new SimulationException("Could not locate string Max_Step:");
			
			maxStep = new Integer(scanner.next());

			input = scanner.next();
			if (input.compareTo("Show_Oct_Tree:") != 0)
				throw new SimulationException("Could not locate string Show_Oct_Tree:");
			
			if (scanner.next().toLowerCase().charAt(0) == 't')
				showOctTree = true;
			else
				showOctTree = false;

			input = scanner.next();
			if (input.compareTo("Strict_Conservation:") != 0)
				throw new SimulationException("Could not locate string Strict_Conservation:");
			
			if (scanner.next().toLowerCase().charAt(0) == 't')
				breakOnEnergyNotConserved = true;
			else
				breakOnEnergyNotConserved = false;

			input = scanner.next();
			if (input.compareTo("Output_Conservation_Info:") != 0)
				throw new SimulationException("Could not locate string Output_Conservation_Info:");
			
			if (scanner.next().toLowerCase().charAt(0) == 't')
				outputConservationInfo = true;
			else
				outputConservationInfo = false;
		}
		catch (Exception ex) {
			throw new SimulationException(ex.getMessage());
		}
	}
	
	// Reads a particle in format "P mass < position > < velocity > radius color"
	// Position and velocity given as a space-separated tuple i.e. "< 0 0 0 >" is the origin
	// Color can be a one of the following string values:
	//    grey, white, red, green, blue
	private void readParticle(String input) throws SimulationException   {
		Scanner scanner;
		Vector3D position;
		Vector3D velocity;
		double x, y, z;
		double mass;
		double radius;
		char firstChar;
		String colorString;
		Color color;
		Particle particle;

		scanner = new Scanner(input);
		
		firstChar = scanner.next().toLowerCase().charAt(0);

		if (firstChar == '#') {
			scanner.close();
			return;
		}
		else if (firstChar != 'p') {
			scanner.close();
			throw new SimulationException("Bad format when reading a particle line");
		}
		
		mass = scanner.nextDouble();
		
		scanner.next(); // eat up '<'
		x = scanner.nextDouble();
		y = scanner.nextDouble();
		z = scanner.nextDouble();
		position = new Vector3D(x, y, z);
		scanner.next(); // eat up '>' 
		
		scanner.next(); // eat up '<'
		x = scanner.nextDouble();
		y = scanner.nextDouble();
		z = scanner.nextDouble();
		velocity = new Vector3D(x, y, z);
		scanner.next(); // eat up '>' 
		
		radius = scanner.nextDouble();
		
		colorString = scanner.next();
		
		if (colorString.toLowerCase().compareTo("red") == 0)
			color = Color.red;
		else if (colorString.toLowerCase().compareTo("green") == 0)
			color = Color.green;
		else if (colorString.toLowerCase().compareTo("blue") == 0)
			color = Color.blue;
		else if (colorString.toLowerCase().compareTo("white") == 0)
			color = Color.white;
		else if (colorString.toLowerCase().compareTo("grey") == 0)
			color = Color.gray;
		else
			color = Color.white;
		
		particle = new Particle(position, velocity, mass, radius);
		particle.setColor(color);
		
		particles.add(particle);
		particle.print();
	
		scanner.close();
	}
	
	private String optionsString() {
		String options = "";

		options += String.format("Gravitational_Constant:  %.15f\n", gravitationalConstant);
		options += String.format("Conservation_Tolerance:  %.15f\n", conservationTolerance);
		options += String.format("Barnes_Hut_Theta:        %.15f\n", barnesHutTheta);
		options += String.format("Time_Step:               %.15f\n", timeStep);
		options += String.format("Max_Step:                 %d\n", maxStep);
		options += "Show_Oct_Tree:            " + showOctTree + "\n";
		options += "Strict_Conservation:      " + breakOnEnergyNotConserved + "\n";
		options += "Output_Conservation_Info: " + outputConservationInfo + "\n\n";
		
		return options;
	}
	
	private void printInput() {
		System.out.println("Starting simulation with the following parameters...");
		System.out.printf("Gravitational_Constant:  %.15f\n", gravitationalConstant);
		System.out.printf("Conservation_Tolerance:  %.15f\n", conservationTolerance);
		System.out.printf("Barnes_Hut_Theta:        %.15f\n", barnesHutTheta);
		System.out.printf("Time_Step:               %.15f\n", timeStep);
		System.out.printf("Max_Step:                 %d\n", maxStep);
		System.out.printf("Show_Oct_Tree:            " + showOctTree + "\n");
		System.out.printf("Strict_Conservation:      " + breakOnEnergyNotConserved + "\n");
		System.out.printf("Output_Conservation_Info: " + outputConservationInfo + "\n");
		
		if (!particles.isEmpty())
			System.out.println("Particles");
		
		for (Particle particle : particles)
			particle.print();
	}

}
