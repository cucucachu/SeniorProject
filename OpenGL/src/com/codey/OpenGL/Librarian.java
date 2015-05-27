package com.codey.OpenGL;

import java.io.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.zip.ZipOutputStream;
import java.text.SimpleDateFormat;

public class Librarian {

	private static String conservationFileName;
	private static String energyFileName;
	private static String simulationFileName;
	private static String performanceFileName;
	
	private Writer conservationWriter;
	private Writer performanceWriter;
	private Writer energyWriter;
	private ZipOutputStream conservationZipper;
	private ZipOutputStream performanceZipper;
	private ZipOutputStream energyZipper;
	
	private ArrayList<Particle> particles;
	private String optionsString;
	private Conservationist colin;
	
	private long lastTime;
	private boolean zipFiles;
	
	public Librarian(ArrayList<Particle> particles, Conservationist colin) 
		throws FileNotFoundException, UnsupportedEncodingException {
		
		this.particles = particles;
		this.colin = colin;
		
		zipFiles = false;
		conservationWriter = null;
		performanceWriter = null;
		energyWriter = null;
		conservationZipper = null;
		performanceZipper = null;
		energyZipper = null;
		optionsString = null;
	}
	
	public void start() throws UnsupportedEncodingException, FileNotFoundException {
		if (conservationFileName.toLowerCase().compareTo("none") != 0) {
			//File conservationFile = new File(addDateToFileName(conservationFileName));
			File conservationFile = new File(conservationFileName);
			conservationWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(conservationFile), "UTF-8"));
		}
		
		if (performanceFileName.toLowerCase().compareTo("none") != 0) {
			File performanceFile = new File(addDateToFileName(performanceFileName));
			performanceWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(performanceFile), "UTF-8"));
		}
		
		if (energyFileName.toLowerCase().compareTo("none") != 0) {
			//File energyFile = new File(addDateToFileName(energyFileName));
			File energyFile = new File(energyFileName);
			energyWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(energyFile), "UTF-8"));
		}	
		
		lastTime = System.nanoTime();
		
	}

	/*  ------------------------------------------------------------
	 *  File Name Setting Methods
	 *  ------------------------------------------------------------
	 */
	
	public void setConservationFileName(String path) {
	    conservationFileName = path;
	}
	
	public void setPerformanceFileName(String path) {
		performanceFileName = path;
	}
	
	public void setSimulationFileName(String path) {
		simulationFileName = path;
	}
	
    public void setEnergyFileName(String path) {
		energyFileName = path;
	}
    
    public void setOptionsString(String optionsString) {
    	this.optionsString = optionsString;
    }
	
	public String addDateToFileName(String file) {
		String extension = file.substring(file.indexOf('.'));
		String name = file.substring(0, file.indexOf('.'));
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		
		return name + "_" + format.format(date) + extension;
	}
	/*  ------------------------------------------------------------
	 *  File Writing Methods
	 *  ------------------------------------------------------------
	 */
	
	public String recordSimulation() throws IOException {
		BufferedWriter simulationWriter;
		
		if (simulationFileName.toLowerCase().compareTo("none") != 0) {
			//File simulationFile = new File(addDateToFileName(simulationFileName));
			File simulationFile = new File(simulationFileName);
			simulationWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(simulationFile), "UTF-8"));	
			
			if (optionsString != null)
				simulationWriter.write(optionsString);
			
			simulationWriter.write("Particles:\n");
			for (Particle particle : particles)
				simulationWriter.write(particle.toString());	
			
			simulationWriter.close();
			return simulationFile.getAbsolutePath();
		}
		
		return null;
		
	}
	
	public void recordPerformance(int step) throws IOException {
		long now;

		if (performanceWriter == null)
			return;
		
		now = System.nanoTime();
		
		if (step == 0) {
			performanceWriter.write("Step, Time since last step (ns) \n");
			performanceWriter.write("0, 0\n");
		}
		else 
			performanceWriter.write(String.format("%d, %d\n", step, now - lastTime));
			
		lastTime = now;
	}
	
	public void recordConservation(int step) throws IOException {
		if (conservationWriter == null)
			return;
		
		if (step == 0)
			conservationWriter.write("Step, Energy, Linear Momentum, Angular Momentum\n");
		

		conservationWriter.write(String.format("%d", step));
		conservationWriter.write(String.format(", %f", colin.energyDeviation() * 100));
		conservationWriter.write(String.format(", %f", colin.linearMomentumDeviation() * 100));
		conservationWriter.write(String.format(",  %f", colin.angularMomentumDeviation() * 100));
		conservationWriter.write("\n");
	}
	
	public void recordEnergy(int step) throws IOException {
		if (energyWriter == null)
			return;
		
		if (step == 0)
			energyWriter.write("Step, Total Energy, Kinetic Energy, Potential Energy\n");
		

		energyWriter.write(String.format("%d", step));
		energyWriter.write(String.format(", %f", colin.energy()));
		energyWriter.write(String.format(", %f", colin.kineticEnergy() * 100));
		energyWriter.write(String.format(", %f", colin.potentialEnergy() * 100));
		energyWriter.write("\n");
	}
	
	public void setZipFiles(Boolean zip) {
		zipFiles = zip;
	}
	
	public void janitor() throws IOException {
		if (conservationWriter != null)
			conservationWriter.close();

		if (performanceWriter != null)
			performanceWriter.close();

		if (energyWriter != null)
			energyWriter.close();
	}
}
