package assistant;

import java.util.concurrent.locks.Condition;

import misc.Exam;
import misc.ExamStack;
import misc.ExamState;

public class Assistant extends Thread {

	private ExamStack stackTODO;
	private ExamStack stackPASSON;
	private ExamStack stackCorrected;
	private Condition alertProf;
	private Condition wait;
	
	private int exercise;
	
	public Assistant(ExamStack toDO, ExamStack passON,ExamStack corrected, int exercise) {
		this.stackTODO = toDO;
		wait = stackTODO.getWait();
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
			this.stackTODO.notify();
			this.stackTODO = stackTODO;
			stackTODO.notifyAll();	
	}

	public ExamStack getStackPASSON() {
		return stackPASSON;	
	}

	public void setStackPASSON(ExamStack stackPASSON) {
		this.stackPASSON = stackPASSON;
	}
	
	private void correct() throws InterruptedException {
		Exam examToCorrect = null;
		synchronized (wait) {
			while (stackTODO.size() == 0) {
				wait.wait();	
			}
		}
		examToCorrect = stackTODO.dequeue();
		synchronized (alertProf) {
			if (examToCorrect.getState() != ExamState.IN_PROGRES) {
				stackCorrected.enqueue(examToCorrect);
				alertProf.notify();
	//				System.out.println("exam corrected: " + stackCorrected.size());
			} else {
				System.out.println("Kaese");
				examToCorrect.correct(exercise);
				stackPASSON.enqueue(examToCorrect);
				alertProf.notify();
			}
		}
	}

	@Override
	public void run() {
			while (!(Thread.interrupted())) {
				try {
					if (gotWork()) {
						correct();
					} else {
							stackTODO.wait();
					}
				} catch (InterruptedException e) {
					// TODO ask prof for termination
					break;
				}
			}
			System.out.println("Feierabend f�r " + Thread.currentThread().getName());
	}

	public void setAlertProf(Condition alertProf) {
		this.alertProf = alertProf;
	}
}
