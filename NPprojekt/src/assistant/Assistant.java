package assistant;

import misc.Exam;
import misc.ExamStack;
import misc.ExamState;

public class Assistant extends Thread {

	private ExamStack stackTODO;
	private ExamStack stackPASSON;
	private ExamStack stackCorrected;

	private int exercise;

	public Assistant(ExamStack toDO, ExamStack passON, ExamStack corrected,
			int exercise) {
		setName("Assistent " + exercise);
		this.stackTODO = toDO;
		this.stackPASSON = passON;
		this.stackCorrected = corrected;
		this.exercise = exercise;
	}

	public boolean gotWork() {
		return stackTODO.size() > 0;
	}

	public ExamStack getStackTODO() {
		return stackTODO;
	}

	public void setStackTODO(ExamStack stackTODO) {
		this.stackTODO = stackTODO;
	}

	public ExamStack getStackPASSON() {
		return stackPASSON;
	}

	public void setStackPASSON(ExamStack stackPASSON) {
		this.stackPASSON = stackPASSON;
	}

	private void correct() throws InterruptedException {
		Exam examToCorrect = null;
		examToCorrect = stackTODO.dequeue();
		if (examToCorrect != null) {
			examToCorrect.correct(exercise);
			if (examToCorrect.getState().equals(ExamState.CORRECTED)) {
				stackCorrected.enqueue(examToCorrect);
			} else {
				stackPASSON.enqueue(examToCorrect);
			}
		}
	}

	@Override
	public void run() {
		System.out.println(stackTODO.size());
		while (!(isInterrupted())) {
			try {
				correct();
			} catch (InterruptedException e) {
				// TODO ask prof for termination
				break;
			}
		}
		System.out
				.println(Thread.currentThread().getName() + " Hausezeit!");
	}
}
