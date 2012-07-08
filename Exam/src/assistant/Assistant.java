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
	
	private int exercise;
	
	public Assistant(ExamStack toDO, ExamStack passON,ExamStack corrected, int exercise) {
		this.stackTODO = toDO;
		this.stackPASSON = passON;
		this.stackCorrected = corrected;
		this.exercise = exercise;
	}
	
	public synchronized boolean gotWork() {
		synchronized (stackTODO) {
			return stackTODO.size() > 0;	
		}
	}
	
	
	public synchronized ExamStack getStackTODO() {
		synchronized (stackTODO) {
			return stackTODO;	
		}
	}

	public synchronized void setStackTODO(ExamStack stackTODO) {
		synchronized (this.stackTODO) {
			synchronized (stackTODO) {
				this.stackTODO.notifyAll();
				this.stackTODO = stackTODO;
				stackTODO.notifyAll();	
			}
		}
	}

	public synchronized ExamStack getStackPASSON() {
		synchronized (stackPASSON) {
			return stackPASSON;	
		}
	}

	public synchronized void setStackPASSON(ExamStack stackPASSON) {
		synchronized (this.stackPASSON) {
			synchronized (stackPASSON) {
				this.stackPASSON = stackPASSON;
			}
		}
	}
	
	private void correct() throws InterruptedException {
		Exam examToCorrect = null;
		synchronized (stackTODO) {
			while (stackTODO.isEmpty()) {
				stackTODO.wait(100);
			}
			examToCorrect = stackTODO.pollFirst();
		}
		if (examToCorrect.getState() != ExamState.IN_PROGRES) {
			synchronized (stackCorrected) {
				stackCorrected.addLast(examToCorrect);
				alertProf.signalAll();
//				System.out.println("exam corrected: " + stackCorrected.size());
			}
		} else {
			examToCorrect.correct(exercise);
			synchronized (stackPASSON) {
				stackPASSON.addLast(examToCorrect);
				alertProf.signalAll();
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
							synchronized (stackTODO) {
								stackTODO.wait();
							}
					}
				} catch (InterruptedException e) {
					// TODO ask prof for termination
					break;
				}
			}
			System.out.println("Feierabend für " + Thread.currentThread().getName());
	}

	public void setAlertProf(Condition alertProf) {
		this.alertProf = alertProf;
	}
}
