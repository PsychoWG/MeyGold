package professor;

import java.util.LinkedList;

import main.Start;
import misc.Exam;
import misc.ExamStack;
import misc.ExamState;
import assistant.Assistant;

public class Professor extends Thread {

	private final ExamStack stackcorrected;
	private final ExamStack stackfinished;
	private LinkedList<Assistant> assistants;



	public Professor(ExamStack corrected, ExamStack finished,
			LinkedList<Assistant> assistants) {
		setName("Professor");
		this.stackcorrected = corrected;
		this.stackfinished = finished;
		this.assistants = assistants;
	}

	// TODO implement CheckAll for b)

	public void checkForFeierabend() {
		for (int i = 0; i < Start.NUM_ASSISTANTS; i++) {
			Assistant current = assistants.get(i);
			synchronized (current) {
				current.interrupt();
				current.notifyAll();
				System.out.println("Thread[" + i
						+ "] wird freundlichst gebeten, sich zu terminieren!");
			}
		}
	}

	@Override
	public void run() {
		while (stackfinished.size() != Start.NUM_EXAMS) {
			Exam examToFinish = stackcorrected.dequeue();
			finishExam(examToFinish);
			stackfinished.enqueue(examToFinish);
			System.out.println("finished");
		}
		checkForFeierabend();
		System.out.println("FEIERABEND!");
	}

	private void startShuffling() {
		// TODO Auto-generated method stub

	}

	private void finishExam(Exam exam) {
		if (exam.getState().equals(ExamState.IN_PROGRES)) {
			throw new IllegalStateException();
		}
		exam.finish();
		stackfinished.enqueue(exam);
		System.out.println(stackfinished.size());
	}

}
