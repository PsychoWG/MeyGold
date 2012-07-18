package main;

import java.util.LinkedList;
import java.util.concurrent.CyclicBarrier;

import misc.Exam;
import misc.ExamStack;
import professor.Professor;
import assistant.Assistant;

public class Start {
	
	public static final int NUM_ASSISTANTS = 2;
	public static final int NUM_EXAMS = 800;
	public static final int NUM_RUNS = 10;
	// TROLOLOLOLOLOOLOLOOLOLOLOLOL
	
	public Start() {
		ExamStack[] stacks = new ExamStack[NUM_ASSISTANTS];
		for (int i = 0; i < NUM_ASSISTANTS; i++) {
			ExamStack current = new ExamStack();
			for (int n = 0; n < NUM_EXAMS/NUM_ASSISTANTS; n++) {
				current.enqueue(new Exam(NUM_ASSISTANTS));
			}

			stacks[i] = current;
		}
		ExamStack corrected = new ExamStack();
		ExamStack finished = new ExamStack();
		
		CyclicBarrier barrier = new CyclicBarrier(NUM_ASSISTANTS);
		
		LinkedList<Assistant> assistants = new LinkedList<Assistant>();
		for (int i = 0; i < NUM_ASSISTANTS; i ++) {
			assistants.add(new Assistant(barrier, stacks[i % NUM_ASSISTANTS], stacks[(i+1) % NUM_ASSISTANTS], corrected, i));
		}
		
		Professor professor = new Professor(corrected, finished, assistants);
		professor.start();
		
		for (int i = 0; i < assistants.size(); i++) {
			assistants.get(i).start();
		}
	}
	
	public static void main(String[] args) {
		new Start();
	}
}
