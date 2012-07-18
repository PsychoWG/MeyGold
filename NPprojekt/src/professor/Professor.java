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
				boolean waitForWork = false;
				if (stackcorrected.isEmpty()) {
					waitForWork = startShuffling();
				} else {
					examToFinish = stackcorrected.dequeue();
					finishExam(examToFinish);
				}
				if (waitForWork) {
					examToFinish = stackcorrected.dequeue();
					finishExam(examToFinish);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		checkForFeierabend();
		System.out.println("FEIERABEND!");
	}

	private boolean startShuffling() {
		boolean shuffling = false;
		int workless = -1;
		for (int i = assistants.size() - 1; i >=0; i--) {
			shuffling = shuffling ? shuffling : !assistants.get(i).gotWork();
		}
		if (!shuffling) {
			return !shuffling;
		} else {
			System.out.println("Everyday I'm shuffling!");
		}
		
		return true;
	}

	private void finishExam(Exam exam) {
		if (exam.getState().equals(ExamState.IN_PROGRES)) {
			throw new IllegalStateException();
		}
		exam.finish();
		stackfinished.enqueue(exam);
		System.out.println("finished " + stackfinished.size());
	}

}
