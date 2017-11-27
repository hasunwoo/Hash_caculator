package filecompare;

/**
 *
 * @author hasunwoo
 * 이 인터페이스는 FileHashCaculator 에서 현재 계산하고있는 해시값의 진행률을 listen하기위한 인터페이스입니다.
 */
public interface IProgressUpdateNotifier {
	public void updateProgress(int progress);
}
