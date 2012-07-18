package misc;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
		Exam ex = null;
		try {
			lock.lock();
			while (stack.isEmpty()) {
				empty.await();
			}
			ex = stack.removeFirst();
		} finally {
			lock.unlock();
		}
			return ex;
	}

	public List<Exam> tail() {
		try {
			lock.lock();
			if (stack.size() > 1) {
				return stack.subList(1, stack.size() - 1);
			} else {
				return null;
			}
		} finally {
			lock.unlock();
		}
	}

	public int size() {
		return stack.size();
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}
}
