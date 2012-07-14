package main;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;

import misc.Exam;
import misc.ExamStack;
import professor.Professor;
import assistant.Assistant;

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
				current.enqueue(new Exam(NUM_ASSISTANTS));
			}
			stacks[i] = current;
		}
		ExamStack corrected = new ExamStack();
		ExamStack finished = new ExamStack();
		
		// Da die Korrekturzeit mit dem HashWert skaliert, sollte man den Prof zuerst implementieren,
		// damit er den kleineren hat und schneller wird.
		Professor professor = new Professor(corrected, finished);
		
		Condition wait = finished.getWait();
		
		List<Assistant> assistants = new LinkedList<Assistant>();
		for (int i = 0; i < NUM_ASSISTANTS; i ++) {
			assistants.add(new Assistant(stacks[i % NUM_ASSISTANTS], stacks[(i+1) % NUM_ASSISTANTS], corrected, i));
		}
		
		Condition profAlert = professor.getDatFreakingCondition(assistants);

		for (int i = 0; i < NUM_ASSISTANTS; i ++) {
			assistants.get(i).setAlertProf(profAlert);
		}
		professor.start();
		long now = System.nanoTime();
		for (int i = 0; i < assistants.size(); i++) {
			assistants.get(i).start();
		}
			synchronized (wait) {
				try {
					while (finished.size() != NUM_EXAMS){
						wait.wait();
					}	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}	
		for (int i = 0; i < NUM_ASSISTANTS; i ++) {
			Assistant current = assistants.get(i);
			synchronized (current) {
				current.interrupt();
				current.notifyAll();
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
