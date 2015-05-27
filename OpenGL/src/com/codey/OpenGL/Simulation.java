package com.codey.OpenGL;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.lwjgl.glfw.GLFWErrorCallback;

public class Simulation {

	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	public static final int FRAME_RATE = 32;
	public static final int DEBOUNCE = 2;
	
	private Painter picaso;
	private CameraMan carl;
	private Conservationist colin;
	private Librarian linda;
	private KeyboardHandler keyboard;
	
	public ArrayList<Particle> particles;
	public SSGravitationalForce gravity;
	private static int step = 0;
	
	private static int debounce = 0;
	boolean nonConservative = false;
	
	private static boolean play = true;
	
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
	
    private GLFWErrorCallback errorCallback;
	
	public Simulation(File inputFile) throws Exception {
		particles = new ArrayList<Particle>();
		
		readInputFile(inputFile);
		
		initialize();
		
		simulate();
		
		cleanUp();
	}
	
	private void setDefaults() {			
		conservationTolerance = 1;
		barnesHutTheta = 0;
		collisionThreshold = 0;
		timeStep = 1;
		maxStep = 0;
		recurringSave = 0;
		particleToTrack = 0;
		showOctTree = false;
		breakOnEnergyNotConserved = false;
		graphicsEnabled = false;
		saveScreenshots = false;
		
		conservationFileName = "none";
		performanceFileName = "none";
		simulationFileName = "none";
		energyFileName = "none";
		zipFiles = false;
	}
	
	private void initialize() throws FileNotFoundException, UnsupportedEncodingException {
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.out));
		
		colin = new Conservationist(particles, conservationTolerance, gravitationalConstant);
		
		linda = new Librarian(particles, colin);
		linda.setConservationFileName(conservationFileName);
		linda.setSimulationFileName(simulationFileName);
		linda.setPerformanceFileName(performanceFileName);	
		linda.setEnergyFileName(energyFileName);
		linda.setOptionsString(optionsString());
		linda.start();
		
		
		if (graphicsEnabled) {
			picaso = new Painter(WIDTH, HEIGHT, FRAME_RATE);
			carl = new CameraMan();
			carl.setParticles(particles);
			carl.track(particles.get(particleToTrack));
			keyboard = new KeyboardHandler(picaso, carl, linda);
			glfwSetKeyCallback(picaso.getWindow(), keyboard); 
		}

		
		gravity = new SSGravitationalForce(gravitationalConstant, particles, barnesHutTheta);
	}
	
	private void simulate() throws Exception {	
		render();
		while(!picaso.isCloseRequested());
		picaso.janitor();
		/*
		while(maxStep == 0 || step < maxStep) {

			if (graphicsEnabled) {
				if (picaso.isCloseRequested())
					break;
			}
			
			if (play)
				nextVerletStep();

			if (graphicsEnabled) {
				render();
				//snapShotSave();
				//checkForPause();
			}
			
			if (!conservative())
				break;
			
			if (recurringSave != 0 && step % recurringSave == 0)
				linda.recordSimulation();
			
			//if (step % 100 == 0)
				System.out.printf("Finished step %d\n", step);
			
			//if (saveScreenshots && step % 10 == 0)
				carl.screenshot(String.format("/home/cody/School/SeniorProject/Output/jpgs/screenshot_%05d.jpg", step));
			
			step++;
			
		}
		*/

	}
	
	private void nextStep() throws Exception {
		Vector3D accel[];
		int i;
		
		linda.recordConservation(step);
		linda.recordPerformance(step);
		linda.recordEnergy(step);
		
		if (collisionThreshold > 0);
			detectCollisions();

		accel = new Vector3D[particles.size()];

		i = 0;
		for (Particle particle : particles) {
			accel[i] = gravity.forceOnParticle(particle);
			i++;
		}

		i = 0;
		for (Particle particle : particles) {
			particle.step(timeStep, accel[i]);
			i++;
		}
		
		gravity.updateOctTree();
			
	}
	
	private void nextVerletStep() throws Exception {
		Vector3D accel[];
		Vector3D nextAccel[];
		int i;
		
		linda.recordConservation(step);
		linda.recordPerformance(step);
		linda.recordEnergy(step);
		
		if (collisionThreshold > 0);
			detectCollisions();

		accel = new Vector3D[particles.size()];
		nextAccel = new Vector3D[particles.size()];

		i = 0;
		for (Particle particle : particles) {
			accel[i] = gravity.forceOnParticle(particle);
			i++;
		}

		i = 0;
		for (Particle particle : particles) {
			particle.verletPositionStep(timeStep, accel[i]);
			i++;
		}
		
		i = 0;
		for (Particle particle : particles) {
			nextAccel[i] = gravity.forceOnParticle(particle);
			i++;
		}
	
		i = 0;
		for (Particle particle : particles) {
			particle.verletVelocityStep(timeStep, accel[i], nextAccel[i]);
			i++;
		}
		
		gravity.updateOctTree();
			
	}
	
	
	
	private void render() {
		int i;
		
		picaso.clear();
		carl.setup();
	
		/*
		for (Particle particle : particles)
			picaso.drawParticle(particle);
		
		if (showOctTree)
			picaso.drawOctTree(gravity.getOctTree().getRoot());
		*/
		picaso.drawSquare(0, 0, 100);
		picaso.render();
		
	}
	
	private void detectCollisions() {
		double distance;
		Particle a, b;
		
		for (int i = 0; i < particles.size(); i++) {
			for (int ii = i - 1; ii > 0; ii--) {
				a = particles.get(i);
				b = particles.get(ii);
				distance = a.getPosition().subtract(b.getPosition()).getNorm();
				if (Math.abs(distance) < collisionThreshold) {
					collide(a, b);
					i--;
					break;
				}
			}
		}
	}
	
	private void collide(Particle a, Particle b) {
		double mass;
		double radius;
		Vector3D position;
		Vector3D velocity;
		Vector3D momentumA;
		Vector3D momentumB;
		Particle particle;

		mass = a.getMass() + b.getMass();
		radius = Math.pow(Math.pow(a.getRadius(), 3) + Math.pow(b.getRadius(),  3), 1./3.);
		position = a.getPosition();
		
		momentumA = a.getVelocity().scalarMultiply(a.getMass());
		momentumB = b.getVelocity().scalarMultiply(b.getMass());
		velocity = momentumA.add(momentumB).scalarMultiply(1./mass);
		
		//System.out.println(String.format("Collision between %d and %d", particles.indexOf(a), particles.indexOf(b)));
		//System.out.printf("Va = %f\nVb = %f\nVc = %f\n", a.getVelocity().getNorm(), b.getVelocity().getNorm(), velocity.getNorm());
		
		particle = new Particle(position, velocity, mass, radius);
		particle.setColor(Color.red);
		
		particles.add(particles.indexOf(a),particle);
		particles.remove(a);
		particles.remove(b);
	}
	
	private boolean conservative() {
		boolean nonConservative = false;
		
		if (!colin.energyConserved())
			nonConservative = true;
		
		if (!colin.linearMomentumConserved())
			nonConservative = true;
		
		if (!colin.angularMomentumConserved())
			nonConservative = true;
		
		if (nonConservative && breakOnEnergyNotConserved)
			return false;
		
		return true;
		
	}
	
	private void cleanUp() throws IOException {
		linda.janitor();
		
		if (graphicsEnabled)
			picaso.janitor();
	}
	/*  ------------------------------------------------------------
	 *  Real Time Input Handlers
	 *  ------------------------------------------------------------
	 */
	/*
	private void snapShotSave() throws IOException {		
		if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
			if (debounce++ == 0)
				linda.recordSimulation(optionsString());
			
			if (debounce >= DEBOUNCE)
				debounce = 0;
		}
	}
	
	private void checkForPause() throws IOException {		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if (debounce++ == 0)
				play = play ? false : true;
			
			if (debounce >= DEBOUNCE)
				debounce = 0;
		}
	}
	*/
	/*  ------------------------------------------------------------
	 *  Input File Reading Methods
	 *  ------------------------------------------------------------
	 */
	
	
	private void readInputFile(File inputFile) throws SimulationException, FileNotFoundException {
		Scanner scanner;
		String nextLine;
		
		System.out.println("Reading input...");
		
		scanner = new Scanner(inputFile);
		
		setDefaults();
		
		readParameters(scanner);
		
		while (scanner.hasNextLine()) {
			nextLine = scanner.nextLine();
			if (!nextLine.trim().isEmpty())
				readParticle(nextLine);
		}

		//printInput();
		
	}
	
	private void readParameters(Scanner scanner) throws SimulationException {
		String input;
		
		if (!scanner.hasNext())
			throw new SimulationException("Input file is empty");
		
		input = scanner.next();

		if (input.compareTo("Gravitational_Constant:") != 0)
			throw new SimulationException("Input file must start with string \"Gravitational_Constant:\"");
		
		gravitationalConstant = new Double(scanner.next());
		
		input = scanner.next();
		
		while (input.toLowerCase().compareTo("particles:") != 0) {

			if (input.charAt(0) == '#') {
				scanner.nextLine();
				input = scanner.next();
				continue;
			}
			
			if (input.compareTo("Conservation_Tolerance:") == 0)
				conservationTolerance = new Double(scanner.next());
			else if (input.compareTo("Barnes_Hut_Theta:") == 0)
				barnesHutTheta = new Double(scanner.next());
			else if (input.compareTo("Collision_Threshold:") == 0)
				collisionThreshold = new Double(scanner.next());
			else if (input.compareTo("Time_Step:") == 0)
				timeStep = new Double(scanner.next());
			else if (input.compareTo("Max_Step:") == 0)
				maxStep = new Integer(scanner.next());
			else if (input.compareTo("Particle_To_Track:") == 0)
				particleToTrack = new Integer(scanner.next());
			else if (input.compareTo("Recurring_Save:") == 0)
				recurringSave = new Integer(scanner.next());
			else if (input.compareTo("Show_Oct_Tree:") == 0) {	
				if (scanner.next().toLowerCase().charAt(0) == 't')
					showOctTree = true;
				else
					showOctTree = false;
			}
			else if (input.compareTo("Strict_Conservation:") == 0) {
				if (scanner.next().toLowerCase().charAt(0) == 't')
					breakOnEnergyNotConserved = true;
				else
					breakOnEnergyNotConserved = false;
			}
			else if (input.compareTo("Graphics_Enabled:") == 0) {
				if (scanner.next().toLowerCase().charAt(0) == 't')
					graphicsEnabled = true;
				else
					graphicsEnabled = false;
			}
			else if (input.compareTo("Save_Screenshots:") == 0) {
				if (scanner.next().toLowerCase().charAt(0) == 't')
					saveScreenshots = true;
				else
					saveScreenshots = false;
			}
			else if (input.compareTo("Conservation_File_Path:") == 0)
				conservationFileName = scanner.next();
			else if (input.compareTo("Simulation_File_Path:") == 0)
				simulationFileName = scanner.next();
			else if (input.compareTo("Performance_File_Path:") == 0)
				performanceFileName = scanner.next();
			else if (input.compareTo("Energy_File_Path:") == 0)
				energyFileName = scanner.next();
			else
				throw new SimulationException(
					String.format("Bad input file format, could not match setting for string \"%s\"", input)
				);
			
			input = scanner.next();
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
		//particle.print();
	
		scanner.close();
	}

	/*  ------------------------------------------------------------
	 *  Printing Methods
	 *  ------------------------------------------------------------
	 */
	
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
	
	private void printInput() {
		System.out.println("Starting simulation with the following parameters...");
		System.out.printf("Gravitational_Constant:  %.15f\n", gravitationalConstant);
		System.out.printf("Conservation_Tolerance:  %.15f\n", conservationTolerance);
		System.out.printf("Barnes_Hut_Theta:        %.15f\n", barnesHutTheta);
		System.out.printf("Time_Step:               %.15f\n", timeStep);
		System.out.printf("Max_Step:                 %d\n", maxStep);
		System.out.printf("Show_Oct_Tree:            " + showOctTree + "\n");
		System.out.printf("Strict_Conservation:      " + breakOnEnergyNotConserved + "\n");
		
		if (!particles.isEmpty())
			System.out.println("Particles");
		
		for (Particle particle : particles)
			particle.print();
	}

}
