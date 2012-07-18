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
		int i = 0;
		for (Assistant assistant : assistants) {
			assistant.interrupt();
			System.out.println("Assistent[" + i 
					+ "] wird freundlichst gebeten, sich zu terminieren!");
			i++;
		}
	}

	@Override
	public void run() {
		while (stackfinished.size() != Start.NUM_EXAMS) {
			Exam examToFinish = null;
			try {
				examToFinish = stackcorrected.dequeue();
			finishExam(examToFinish);
			System.out.println("finished");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
