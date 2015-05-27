package com.codey.OpenGL;

import java.io.*;



public class Launcher {
	
	public static final int WIDTH = 1920;//= 800;
	public static final int HEIGHT = 1080;//600;
	public static final int FRAME_RATE = 32;
	
	public static Painter picaso;
	public static CameraMan carl;
	
	public static SolarSystem solarSystem;
	public static GalaxyMerger galaxyMerger;
	public static Simulation simulation;
	public static SimulationCreator simulationCreator;
	
	public static void main(String[] args) {
		try {
			launch();
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			return;
		}
	}
	
	public static void launch() throws Exception {

		LibraryLoader.loadNativeLibrary();

		simulationCreator = new SimulationCreator();
		
		simulation = new Simulation(new File(simulationCreator.getSavedFilePath()));
		//simulation = new Simulation(new File("/home/cody/School/SeniorProject/Simulations/GalaxyMerger.sim"));		
		//simulation = new Simulation(new File("/home/cody/School/SeniorProject/Simulations/SolarSystem.sim"));
		
	}
}
