package com.verisjudge.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;

public class ProcessHelper {

	private final List<String> commandArgs;
	private final ProcessBuilder processBuilder;
	private long timeoutMillis = 0;
	
	public ProcessHelper(List<String> commandArgs) {
		this.commandArgs = commandArgs;
		processBuilder = new ProcessBuilder();
	}
	
	public ProcessHelper redirectError(File file) {
		processBuilder.redirectError(file);
		return this;
	}
	
	public ProcessHelper redirectOutput(File file) {
		processBuilder.redirectOutput(file);
		return this;
	}
	
	public ProcessHelper redirectInput(File file) {
		processBuilder.redirectInput(file);
		return this;
	}
	
	public ProcessHelper directory(File file) {
		processBuilder.directory(file);
		return this;
	}
	
	public ProcessHelper setTimeout(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
		return this;
	}
	
	public ExecutionResult run() throws IOException, InterruptedException {
		Process process;
		boolean useTimeProcess = SystemUtils.IS_OS_UNIX;
		File timeOutputFile = null;
		long startNanos, endNanos;
		try {
			List<String> args = new ArrayList<>();
			if (useTimeProcess) {
				timeOutputFile = Files.createTempFile("time", ".txt").toFile();
				args.addAll(getTimeProcessArgs(timeOutputFile));
			}
			args.addAll(commandArgs);

			process = processBuilder.command(args).start();
			startNanos = System.nanoTime();
		} catch (IOException e) {
			throw e;
		}

		int exitValue;
		try {
			if (timeoutMillis > 0) {
				boolean completed = process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS);
				if (!completed) {
					return new ExecutionResult(false, 0, 0, 0);
				}
				exitValue = process.exitValue();
			} else {
				exitValue = process.waitFor();
			}
			endNanos = System.nanoTime();
		} catch (InterruptedException e) {
			process.destroyForcibly();
			throw e;
		}
		if (useTimeProcess) {
			try {
				Scanner timeResultsScanner = new Scanner(timeOutputFile);
				while (!timeResultsScanner.next().equals("real")) {
					continue;
				}
				long realTime =  Math.round(timeResultsScanner.nextDouble() * 1000);
				timeResultsScanner.next();
				long userTime = Math.round(timeResultsScanner.nextDouble() * 1000);
				timeResultsScanner.next();
				long sysTime = Math.round(timeResultsScanner.nextDouble() * 1000);
				timeResultsScanner.close();
				
				long runtime = Math.min(realTime, userTime + sysTime);
				long idleTime = Math.max(0, realTime - runtime);

				return new ExecutionResult(true, exitValue, runtime, idleTime);
			} catch (IOException e) {
				throw e;
			}
		} else {
			long runtime = (endNanos - startNanos + 999) / 1000;
			return new ExecutionResult(true, exitValue, runtime, 0);
		}
	}
	
	/**
     * Returns the process arguments required to time a process and have
     * the output redirected to the file provided.
     * @param outputFilename
     * @return The list of arguments to time the process.
     */
    public static List<String> getTimeProcessArgs(File outputFile) {
    	return List.of("time", "-p", "--quiet", "-o", outputFile.getAbsolutePath());
    }
    
    public class ExecutionResult {
    	private final boolean completed;
    	private final int exitValue;
    	private final long runtime;
    	private final long idleTime;
    	
    	public ExecutionResult(boolean completed, int exitValue, long runtime, long idleTime) {
    		this.completed = completed;
    		this.exitValue = exitValue;
    		this.runtime = runtime;
    		this.idleTime = idleTime;
    	}
    	
    	public boolean completed() {
    		return completed;
    	}

    	public int exitValue() {
    		return exitValue;
    	}

    	public long runtime() {
    		return runtime;
    	}

    	public long idleTime() {
    		return idleTime;
    	}
    }
	
}
