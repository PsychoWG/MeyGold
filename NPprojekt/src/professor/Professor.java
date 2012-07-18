package professor;

import java.util.LinkedList;

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

	public void checkAssistants() {
		for (Assistant assistant : assistants) {
			if (assistant.isWorking()) {
				return;
			}
		}
		int i = 0;
		for (Assistant assistant : assistants) {
			assistant.interrupt();
			System.out.println("Assistent[" + i + "] stop working!");
			i++;
		}
		System.out.println("i gonna kill myself now!");
		interrupt();
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			Exam examToFinish = null;
			try {
				if (stackcorrected.isEmpty()) {
					checkAssistants();
				} else {
					examToFinish = stackcorrected.dequeue();
					finishExam(examToFinish);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Professor stop working");
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
		System.out.println("Exam finished! " + stackfinished.size());
	}

}
