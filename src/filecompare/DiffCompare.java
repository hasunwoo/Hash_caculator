package filecompare;

import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;

/**
 *
 * @author hasunwoo
 * 파일 해시계산기의 GUI를 제공해줍니다
 * windowbuilder pro 를 이용해서 만들었습니다.
 */
public class DiffCompare extends JFrame {
	public DiffCompare() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("파일비교 V1.0 by hasun");
		setResizable(false);
		setSize(687, 410);

		setfile1 = new JButton("파일1 설정");
		setfile1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fname1.setText(selectFile());
			}
		});
		setfile1.setBounds(546, 123, 123, 23);

		JLabel subject = new JLabel("파일비교기 V1.0");
		subject.setBounds(226, 36, 207, 33);
		subject.setFont(new Font("굴림", Font.PLAIN, 28));

		reset1 = new JButton("파일1 초기화");
		reset1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f1 = null;
				fname1.setText("");
				updateHashButton();
			}
		});
		reset1.setBounds(546, 90, 123, 23);

		reset2 = new JButton("파일2 초기화");
		reset2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				f2 = null;
				fname2.setText("");
				updateHashButton();
			}
		});
		reset2.setBounds(546, 196, 123, 23);
		getContentPane().setLayout(null);

		reset = new JButton("전체 초기화");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetAll();
			}
		});
		reset.setBounds(128, 290, 122, 62);
		getContentPane().add(reset);

		start = new JButton("비교하기!!!!");
		start.addActionListener(new ActionListener() {
			private boolean canceled = false;

			public void actionPerformed(ActionEvent e) {
				canceled = false;
				File file1 = new File(fname1.getText());
				File file2 = new File(fname2.getText());
				if (!file1.exists()) {
					JOptionPane.showMessageDialog(DiffCompare.this,
							"파일1 를 찾을수 없습니다", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!file2.exists()) {
					JOptionPane.showMessageDialog(DiffCompare.this,
							"파일2 를  찾을수 없습니다", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					ProgressMonitor pm = new ProgressMonitor(DiffCompare.this,
							"파일비교 진행도", "해시값 계산중", 0, 100);
					pm.setMillisToDecideToPopup(0);
					pm.setMillisToPopup(0);
					Thread worker = new Thread(new Runnable() {
						@Override
						public void run() {
							f1 = null;
							f2 = null;
							updateHashButton();
							setBusy();
							try {
								IProgressUpdate pu = new IProgressUpdate() {
									@Override
									public void updateProgress(int progress) {
										if (pm.isCanceled()) {
											cancelOperation();
										}
										pm.setProgress(progress);
									}
								};
								f1 = new FileHashCaculator(file1, pu);
								f2 = new FileHashCaculator(file2, pu);
								pm.setNote("해시값 계산중 - 파일1");
								f1.start();
								if (canceled)
									throw new Exception("사용자에 의해 취소되었습니다");
								pm.setNote("해시값 계산중 - 파일2");
								f2.start();
								if (canceled)
									throw new Exception("사용자에 의해 취소되었습니다");
								if (f1.equals(f2)) {
									JOptionPane.showMessageDialog(
											DiffCompare.this, "두 파일은 같습니다",
											"파일비교 결과",
											JOptionPane.INFORMATION_MESSAGE);
								} else {
									JOptionPane.showMessageDialog(
											DiffCompare.this, "두 파일은 다릅니다",
											"파일비교 결과",
											JOptionPane.INFORMATION_MESSAGE);
								}
							} catch (Exception ex) {
								JOptionPane.showMessageDialog(
										DiffCompare.this,
										ex.getClass().getName() + ":"
												+ ex.getMessage(), "오류",
										JOptionPane.ERROR_MESSAGE);
								resetAll();
							} finally {
								setIdle();
								pm.close();
								updateHashButton();
							}
						}

						private void cancelOperation() {
							canceled = true;
							if (f1 != null) {
								f1.cancel();
							}
							if (f2 != null) {
								f2.cancel();
							}
							f1 = null;
							f2 = null;
						}
					});
					worker.setName("file-compare");
					worker.start();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(DiffCompare.this, ex
							.getClass().getName() + ":" + ex.getMessage(),
							"오류", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		start.setBounds(299, 289, 122, 64);
		getContentPane().add(start);
		getContentPane().add(reset1);
		getContentPane().add(setfile1);

		setfile2 = new JButton("파일2 설정");
		setfile2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fname2.setText(selectFile());
			}
		});
		setfile2.setBounds(546, 229, 123, 23);
		getContentPane().add(setfile2);
		getContentPane().add(reset2);
		getContentPane().add(subject);

		JLabel file1 = new JLabel("파일1");
		file1.setFont(new Font("굴림", Font.PLAIN, 20));
		file1.setBounds(12, 108, 57, 22);
		getContentPane().add(file1);

		fname1 = new JTextField();
		fname1.setBounds(67, 108, 419, 24);
		getContentPane().add(fname1);
		fname1.setColumns(10);

		JLabel file2 = new JLabel("파일2");
		file2.setFont(new Font("굴림", Font.PLAIN, 20));
		file2.setBounds(12, 212, 57, 22);
		getContentPane().add(file2);

		fname2 = new JTextField();
		fname2.setColumns(10);
		fname2.setBounds(67, 212, 419, 24);
		getContentPane().add(fname2);

		status = new JLabel("");
		status.setBounds(550, 54, 105, 15);
		getContentPane().add(status);

		extraf1 = new JButton("파일1 해시값");
		extraf1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextArea text = new JTextArea(f1.toString());
				text.setEditable(false);
				JOptionPane.showMessageDialog(DiffCompare.this, text,"파일1 속성",JOptionPane.PLAIN_MESSAGE);
			}
		});
		extraf1.setBounds(546, 283, 123, 23);
		getContentPane().add(extraf1);

		extraf2 = new JButton("파일2 해시값");
		extraf2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextArea text = new JTextArea(f2.toString());
				text.setEditable(false);
				JOptionPane.showMessageDialog(DiffCompare.this, text,"파일2 속성",JOptionPane.PLAIN_MESSAGE);
			}
		});
		extraf2.setBounds(546, 330, 123, 23);
		getContentPane().add(extraf2);

		URL iconurl = getClass().getResource("/resources/icon.png");
		ImageIcon icon = new ImageIcon(iconurl);
		setIconImage(icon.getImage());

		updateHashButton();
		setVisible(true);
	}
	/**
	 * 파일선택창을 띄웁니다.
	 * @return 파일의 절대경로
	 */
	public String selectFile() {
		FileDialog fd = new FileDialog(this, "파일선택", FileDialog.LOAD);
		String spr = System.getProperty("file.separator");
		fd.setDirectory("C:" + spr);
		fd.setVisible(true);
		if (fd.getDirectory() == null || fd.getFile() == null) {
			return "";
		}
		return fd.getDirectory() + fd.getFile();
	}

	public void setBusy() {
		status.setText("작업 중입니다");
		reset.setEnabled(false);
		start.setEnabled(false);
		reset1.setEnabled(false);
		reset2.setEnabled(false);
		setfile1.setEnabled(false);
		setfile2.setEnabled(false);
		fname1.setEditable(false);
		fname2.setEditable(false);
	}

	public void setIdle() {
		status.setText("");
		reset.setEnabled(true);
		start.setEnabled(true);
		reset1.setEnabled(true);
		reset2.setEnabled(true);
		setfile1.setEnabled(true);
		setfile2.setEnabled(true);
		fname1.setEditable(true);
		fname2.setEditable(true);
	}

	private void updateHashButton() {
		extraf1.setEnabled(f1 != null);
		extraf2.setEnabled(f2 != null);
	}

	private void resetAll() {
		f1 = null;
		f2 = null;
		fname1.setText("");
		fname2.setText("");
		setIdle();
		updateHashButton();
	}

	private static final long serialVersionUID = -411173112597862444L;
	private JTextField fname1;
	private FileHashCaculator f1 = null;
	private JTextField fname2;
	private FileHashCaculator f2 = null;
	private JLabel status;
	private JButton reset;
	private JButton start;
	private JButton reset1;
	private JButton reset2;
	private JButton setfile1;
	private JButton setfile2;
	private JButton extraf1;
	private JButton extraf2;
}
