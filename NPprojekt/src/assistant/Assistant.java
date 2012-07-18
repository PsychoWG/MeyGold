package assistant;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import misc.Exam;
import misc.ExamStack;
import misc.ExamState;

public class Assistant extends Thread {

	private ExamStack stackTODO;
	private ExamStack stackPASSON;
	private ExamStack stackCorrected;
	private CyclicBarrier barrier;

	private int exercise;
	private boolean working;

	public Assistant(CyclicBarrier barrier, ExamStack toDO, ExamStack passON,
			ExamStack corrected, int exercise) {
		setName("Assistent " + exercise);
		this.stackTODO = toDO;
		this.stackPASSON = passON;
		this.stackCorrected = corrected;
		this.exercise = exercise;
		this.barrier = barrier;
		working = true;
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
		working = false;
		examToCorrect = stackTODO.dequeue();
		working = true;
		if (examToCorrect != null) {
			examToCorrect.correct(exercise);
			if (examToCorrect.getState().equals(ExamState.CORRECTED)) {
				stackCorrected.enqueue(examToCorrect);
			} else {
				stackPASSON.enqueue(examToCorrect);
			}
		}
	}

	public boolean isWorking() {
		return working || stackTODO.size() > 0;
	}

	@Override
	public void run() {
		try {
			barrier.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (BrokenBarrierException e1) {
			e1.printStackTrace();
		}
		System.out.println("Assistent " + exercise + " starts working");
		working = true;
		while (!(isInterrupted())) {
			try {
				correct();
			} catch (InterruptedException e) {
				System.out.println(Thread.currentThread().getName()
						+ " finished!");
				break;
			}
		}
	}
}
