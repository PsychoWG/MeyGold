package main;

import java.util.LinkedList;
import java.util.concurrent.CyclicBarrier;

import misc.Exam;
import misc.ExamStack;
import misc.FinishedStack;
import professor.Professor;
import assistant.Assistant;

public class Start {
	
	public static final int NUM_ASSISTANTS = 2;
	public static final int NUM_EXAMS = 1000;
	
	public Start() {
		long start = System.nanoTime();
		
		// erstelle Klausuren und lege die Stoesse aus
		ExamStack[] stacks = new ExamStack[NUM_ASSISTANTS];
		for (int i = 0; i < NUM_ASSISTANTS; i++) {
			ExamStack current = new ExamStack();
			for (int n = 0; n < NUM_EXAMS/NUM_ASSISTANTS; n++) {
				current.enqueue(new Exam(NUM_ASSISTANTS));
			}

			stacks[i] = current;
		}
		// erstelle Stoesse fuer den Professor
		ExamStack corrected = new ExamStack();
		ExamStack finished = new FinishedStack();
		
		CyclicBarrier barrier = new CyclicBarrier(NUM_ASSISTANTS);
		
		LinkedList<Assistant> assistants = new LinkedList<Assistant>();
		// Setze die Assistenten an den Tisch und weise ihnen Stoesse zu
		// Zeige ihnen ausserdem, wo sie vollkommen korrigierte Klausuren hinlegen sollen (Stoss des Professor)
		for (int i = 0; i < NUM_ASSISTANTS; i ++) {
			assistants.add(new Assistant(barrier, stacks[i % NUM_ASSISTANTS], stacks[(i+1) % NUM_ASSISTANTS], corrected, i));
		}
		
		// Erstelle den Prof und lasse ihn loslegen
		Professor professor = new Professor(corrected, finished, assistants);
		professor.start();
		
		// Gib den Startschuss
		for (int i = 0; i < assistants.size(); i++) {
			assistants.get(i).start();
		}
		
		try {
			professor.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println((System.nanoTime() - start) / 100000000);
	}
	
	public static void main(String[] args) {
		new Start();
	}
}
