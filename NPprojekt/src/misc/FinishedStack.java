package misc;

public class FinishedStack extends ExamStack {

	@Override
	public void enqueue(Exam e) {
		stack.add(e);
	}
}
