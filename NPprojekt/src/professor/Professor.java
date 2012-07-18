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

	/**
	 * Erstellt einen Professor, der in der Lage ist, Klausuren fertig
	 * zu korrigieren, auf Beendigung zu checken und das Ende auszurufen.
	 * 
	 * @param corrected - Der ToDo-{@link ExamStack} des Professors
	 * @param finished - Die fertigen {@link Exam}
	 * @param assistants - Eine Liste der {@link Assistant}
	 */
	public Professor(ExamStack corrected, ExamStack finished,
			LinkedList<Assistant> assistants) {
		setName("Professor");
		this.stackcorrected = corrected;
		this.stackfinished = finished;
		this.assistants = assistants;
	}

	/**
	 * Ueberprueft, ob noch alle arbeiten.
	 * 
	 * Falls alle {@link Assistant}en nicht arbeiten und der ToDo-{@link ExamStack}
	 * des Professors nicht leer ist, prueft diese Methode zusaetzlich, ob ueberhaupt
	 * noch Klausuren nicht auf dem "Finished"-{@link ExamStack} liegen. Findet er keine mehr,
	 * so ruft der Professor das Ende auf.
	 * 
	 * @return	boolean <br />Gibt <u>sofort</u> true zurueck, wenn der Professor
	 * 								selbst noch arbeiten kann. <br />
	 * 			Gibt false zurueck, wenn nur ein Assistent
	 * 							nicht arbeiten, true ansonsten.
	 */
	public boolean checkAssistantsForWork() {
		if (!stackcorrected.isEmpty()) {
			return true;
		}
		boolean gotWork = true;
		boolean checkAll = true;
		for (int i = assistants.size(); i > 0; i--) {
			Assistant assistant = assistants.get(i-1);
			gotWork = gotWork ? assistant.isWorking() : gotWork;
			if (assistant.isWorking()) {
				checkAll = false;
			}
			if (!gotWork && checkAll) {
				assistant.setChecked(true);
			}
		}
		if (!checkAll) {
			return gotWork;
		}
		
		boolean reallyRdy = true;
		for (int i = assistants.size(); i > 0; i--) {
			Assistant assistant = assistants.get(i-1);
			boolean current = assistant.isChecked();
			if (!current) {
				reallyRdy = false;
			}
			assistant.setChecked(false);
		}
		if (!reallyRdy) {
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
					startShuffling();
					if (!isInterrupted()) {
						examToFinish = stackcorrected.dequeue();
						if (examToFinish != null) {
							finishExam(examToFinish);
						}
					} else {
						break;
					}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Professor stop working");
	}

	/**
	 * Schaut nach den {@link Assistant}en.
	 * Wenn alle noch Arbeit haben, wird nichts getan.
	 * Ansonsten holt der {@link Professor} sich alle Klausuren bis auf die oberste
	 * von jedem ToDo-Stack eines jeden Assistenten und verteilt sie uniform gleich.
	 * @return
	 */
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

	/**
	 * Die eigentliche Umverteilungsmethode.
	 * Verteilt alle {@link Exam} der Tails aller {@link ExamStack}
	 * uniform gleich unter allen {@link Assistant}
	 */
	private void shuffle() {
		LinkedList<Exam> toShuffle = new LinkedList<Exam>();
		for (Assistant assistant : assistants) {
			List<Exam> current = assistant.getStackTODO().tail();
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
	
	/**
	 * Fuehrt die finale Korrektur durch.
	 * Akzeptiert nur Klausuren mit {@link ExamState}.CORRECTED
	 * ansonsten wird eine {@link IllegalStateException} geworfen.
	 * 
	 * @param exam
	 * @throws IllegalStateException <br /> {@link Exam} hatte nicht
	 * 					den {@link ExamState}.CORRECTED
	 */
	private void finishExam(Exam exam) {
		if (!exam.getState().equals(ExamState.CORRECTED)) {
			throw new IllegalStateException();
		}
		exam.finish();
		stackfinished.enqueue(exam);
		System.out.println("Exam finished! " + stackfinished.size());
	}

}
