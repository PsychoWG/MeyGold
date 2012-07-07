package main;

import java.util.Arrays;

import professor.Professor;

import assistant.Assistant;

import misc.Exam;
import misc.ExamStack;

public class Start {
	
	public static final int NUM_ASSISTANTS = 10;
	public static final int NUM_EXAMS = 1000;
	// TROLOLOLOLOLOOLOLOOLOLOLOLOL
	
	public Start() {
		ExamStack[] stacks = new ExamStack[NUM_ASSISTANTS];
		for (int i = 0; i < NUM_ASSISTANTS; i++) {
//			Exam[] exams = new Exam[NUM_EXAMS/NUM_ASSISTANTS];
//			Arrays.fill FUELLT DIE DINGER MIT JEWEILS DER GLEICHEN KLAUSUR
//			--> d.h. du hattest vorher immer nur EINE klausur 100 mal
//			Arrays.fill(exams, new Exam(NUM_ASSISTANTS));
			ExamStack current = new ExamStack();
			for (int n = 0; n < NUM_EXAMS/NUM_ASSISTANTS; n++) {
				current.push(new Exam(NUM_ASSISTANTS));
			}
			stacks[i] = current;
		}
		ExamStack corrected = new ExamStack();
		ExamStack finished = new ExamStack();
		
		Assistant[] assistants = new Assistant[NUM_ASSISTANTS];
		for (int i = 0; i < NUM_ASSISTANTS; i ++) {
			assistants[i] = new Assistant(stacks[i % NUM_ASSISTANTS], stacks[(i+1) % NUM_ASSISTANTS], corrected, i);
		}
		
//		Professor professor = new Professor(corrected, finished, NUM_EXAMS);
		
//		professor.start();
		long now = System.nanoTime();
		for (int i = 0; i < assistants.length; i++) {
			assistants[i].start();
		}
		synchronized (corrected) {
			while (corrected.size() != NUM_EXAMS){
				try {
					corrected.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}	
		}
		for (int i = 0; i < NUM_ASSISTANTS; i ++) {
			synchronized (assistants[i]) {
				assistants[i].interrupt();
				assistants[i].notifyAll();
				System.out.println("Thread[" + i + "] wird freundlichst gebeten, sich zu terminieren!");
			}
		}
		for (int n = 0; n < NUM_ASSISTANTS; n++) {
			synchronized (stacks[(n)% NUM_ASSISTANTS]) {
				stacks[n].notifyAll();
			}	
		}
		System.out.println((System.nanoTime() - now)/1000000);
		System.out.println("Alle sollten jetzt fertig sein!");
	}
	
	public static void main(String[] args) {
//		Exam[] exams = new Exam[NUM_EXAMS/NUM_ASSISTANTS];
//		Arrays.fill(exams, new Exam(NUM_ASSISTANTS));
//		System.out.println(Boolean.toString(new Boolean(exams[0].equals(exams[1]))));
		new Start();
	}
}
