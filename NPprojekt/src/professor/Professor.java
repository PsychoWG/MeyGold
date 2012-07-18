package professor;

import java.util.LinkedList;
import java.util.List;

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

	public boolean checkAssistantsForWork() {
		if (!stackcorrected.isEmpty()) {
			return true;
		}
		boolean gotWork = true;
		boolean checkAll = true;
		for (int i = assistants.size(); i > 0; i--) {
			Assistant ass = assistants.get(i-1);
			gotWork = gotWork ? ass.isWorking() : gotWork;
			if (ass.isWorking()) {
				checkAll = false;
			}
		}
		if (!checkAll) {
			return gotWork;
		}
		
		int i = 0;
		for (Assistant assistant : assistants) {
			assistant.interrupt();
			System.out.println("Assistent[" + i + "] stop working!");
			i++;
		}
		System.out.println("i gonna kill myself now!");
		interrupt();
		return true;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			Exam examToFinish = null;
			try {
//				boolean waitForWork = false;
//				if (stackcorrected.isEmpty()) {
//					waitForWork = !startShuffling();
//				} else {
					startShuffling();
					if (!isInterrupted()) {
						examToFinish = stackcorrected.dequeue();
						if (examToFinish != null) {
							finishExam(examToFinish);
						}
					} else {
						break;
					}
//				}
//				if (waitForWork) {
//					if (isInterrupted()) {
//						break;
//					}
//					examToFinish = stackcorrected.dequeue();
//					finishExam(examToFinish);
//				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				break;
			}
		}
		System.out.println("Professor stop working");
	}

	private boolean startShuffling() {
		boolean shuffling = !checkAssistantsForWork();
		if (!shuffling) {
			return true;
		} else {
			shuffle();
			System.out.println("Everyday I'm shuffling!");
		}
		
		return false;
	}

	private void shuffle() {
		LinkedList<Exam> toShuffle = new LinkedList<Exam>();
		for (Assistant ass : assistants) {
			List<Exam> current = ass.getStackTODO().tail();
			if (current != null) {
				toShuffle.addAll(current);
			}
		}
		int counter = 0;
		while (!toShuffle.isEmpty()) {
			assistants.get(counter % assistants.size()).getStackTODO().enqueue(toShuffle.removeFirst());
			counter++;
		}
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
