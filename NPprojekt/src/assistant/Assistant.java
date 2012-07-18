package assistant;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import professor.Professor;

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
	private boolean checked = false;

	/**
	 * Erstellt einen neuen Assistenten. <br />
	 * Dieser benoetigt zwei {@link ExamStack}, einer zum runternehmen
	 * und einer zum drauflegen. <br />
	 * Ausserdem muss er wissen, wo der {@link ExamStack} des {@link Professor}
	 * liegt, um dort vollstaendig korrigiert {@link Exam} draufzulegen.
	 * Die {@link CyclicBarrier} dient zum Synchronstart. 
	 * 
	 * @param barrier - Gibt das Startsignal
	 * @param toDO - zum Runternehmen
	 * @param passON - zum Drauflegen
	 * @param corrected - zum Weglegen korrigierter
	 * @param exercise - zu bearbeitende Aufgabe
	 */
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

	/**
	 * Nimmt die naechste {@link Exam} von stackTODO, schaut nach, ob diese
	 * schon korrigiert ist, wenn nein, korrigiert er seine Aufgabe, ansonsten
	 * legt er sie auf des {@link ExamStack} des {@link Professor}
	 * @throws InterruptedException
	 */
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
				System.out.println(getName()
						+ " finished!");
				break;
			}
		}
	}

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
