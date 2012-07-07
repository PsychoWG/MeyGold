package misc;

import java.util.LinkedList;


public class ExamStack extends LinkedList<Exam> {

	private static final long serialVersionUID = 8241504363882092462L;

	@Override
	public void addLast(Exam e) {
		super.addLast(e);
		notifyAll();
	}
	
	@Override
	public synchronized Exam pollFirst() {
		return super.pollFirst();
	}
//	private Lock lock;
//	private Condition empty;
//
//	public ExamStack(List<Exam> exams) {
//		lock = new ReentrantLock(true);
//		empty = lock.newCondition();
//		if (exams != null) {
//			for (Exam exam : exams) {
//				add(exam);
//			}
//		}
//	}
//	
//
//	public synchronized void putLast(Exam exam) throws InterruptedException {
//		addLast(exam);
////		notifyAll();
//		empty.signalAll();
//	}
//
//	public synchronized Exam pullFirst() throws InterruptedException {
////		lock.lock();
//		while (isEmpty()) {
//			empty.await();
////			empty.
////			wait();
//		}
//		return removeFirst();
//	}
}
