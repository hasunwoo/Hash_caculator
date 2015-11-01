package filecompare;

/**
 *
 * @author hasunwoo
 * 이 클래스는 IProgressUpdate 클래스의 해시값 계산중 취소를 할때 사용하는 인터페이스입니다.
 * 기본적으로 이 인터페이스는 implement된 클래스가 취소될 수 있다는걸 의미합니다.
 */
public interface ICancelable {
	public void cancel();
}
