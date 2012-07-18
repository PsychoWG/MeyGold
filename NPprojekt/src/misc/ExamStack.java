package misc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExamStack {

	private LinkedList<Exam> stack;

	private Lock lock = new ReentrantLock();
	private Condition empty = lock.newCondition();

	public ExamStack() {
		stack = new LinkedList<Exam>();
	}

	public ExamStack(Collection<Exam> exams) {
		stack = new LinkedList<Exam>();
		stack.addAll(exams);
	}

	public void enqueue(Exam e) {
		try {
			lock.lock();
			stack.addLast(e);
			empty.signalAll();
		} finally {
			lock.unlock();
		}
	}

	public Exam dequeue() throws InterruptedException {
		try {
			lock.lock();
			while (stack.isEmpty()) {
				empty.await();
			}
			Exam ex = stack.removeFirst();
			return ex;
		} finally {
			lock.unlock();
		}
	}

	public LinkedList<Exam> tail() {
		return null;
	}

	public int size() {
		return stack.size();
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}
}
