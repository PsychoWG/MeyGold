package misc;


/* Dies ist eine unvollst"andige Implementierung der Exam Klasse aus
 * der Aufgabenstellung. Sie bietet die oeffentlichen Methoden correct
 * und finish an, sowie einige privaten Methoden, die dafuer sorgen,
 * dass tatsaechlich Rechenleistung und Speicher waehrend der Methoden
 * correct und finish verbraucht wird. Dies erleichtert es Ihnen die
 * Effizienz Ihre Implementierung auf verschiedenen (Mulit-Core)
 * Architekturen zu testen.
 * 
 * Sie koennen diese Klasse nach belieben erweitern. Sie sollten
 * an den angegebenen Stellen jedoch keine Veraenderung vornehmen.
 */

public class Exam {

	private ExamState state;

	// false needs correction
	// true corrected
	private volatile boolean[] exercises;

	/*
	 * Veraendert die Dauer der Methoden correct und finish
	 */
	private static final int scale_correct = 1000000;
	private static final int scale_finish = 100000;

	public Exam(int exercises) {
		state = ExamState.IN_PROGRES;
		this.exercises = new boolean[exercises];
//		primitive boolean sind default false
//		Arrays.fill(this.exercises, false);
	}

	/*
	 * Rechenleistung verschwenden! (DO NOT CHANGE THIS METHOD)
	 */
	private static int xorShift(int y) {
		y ^= (y << 6);
		y ^= (y >>> 21);
		y ^= (y << 7);

		return Math.abs(y);
	}

	/*
	 * Rechenleistung verschwenden! (DO NOT CHANGE THIS METHOD)
	 */
	private static int mod(int x, int y) {
		x = Math.abs(x);
		y = Math.abs(y);

		while (x >= y)
			x = x - y;

		return x;
	}

	/*
	 * Rechenleistung verschwenden! (DO NOT CHANGE THIS METHOD)
	 */
	private static boolean spend_time(int n, int s) {
//		return true;
		int y = (xorShift(n)) % s;
		int test = 0;

		for (int i = 2; i < y; i++)
			if (mod(y, i) == 0)
				test = test + 1;

		return (test > 0);
	}

	/*
	 * Rechenleistung verschwenden! (DO NOT CHANGE THIS METHOD)
	 */
	public static void do_correction() {
		int i = Thread.currentThread().hashCode();
		spend_time(i * (i + 12345), scale_correct);
	}

	/*
	 * Rechenleistung verschwenden! (DO NOT CHANGE THIS METHOD)
	 */
	public static void do_finish() {
		int i = Thread.currentThread().hashCode();
		spend_time(i * (i + 12345), scale_finish);
	}

	public synchronized void correct(int exercise) {
		if (state.equals(ExamState.CORRECTED)) {
			throw new IllegalStateException();
		}
		if (!exercises[exercise]) {
			 Exam.do_correction(); // Beansprucht Prozessorleistung und
			// Speicher. Dieser Aufruf muss
			// innerhalb der Methode correct
			// erfolgen. Sie duerfen (und sollten)
			// jedoch beliebigen Programmcode davor
			// und danach einfuegen.
			exercises[exercise] = true;
			updateExamState();
		} else {
			updateExamState();
		}

	}

	public synchronized void updateExamState() {
		for (boolean exercise : exercises) {
			if (!exercise) {
				return;
			}
		}
		state = ExamState.CORRECTED;
	}

	public synchronized ExamState getState() {
		return state;
	}

	public void finish() {
		 Exam.do_finish(); // Beansprucht Prozessorleistung und
		// Speicher. Dieser Aufruf muss
		// innerhalb der Methode finish
		// erfolgen. Sie duerfen (und sollten)
		// jedoch beliebigen Programmcode davor
		// und danach einfuegen.
		state = ExamState.FINISHED;
	}
}
