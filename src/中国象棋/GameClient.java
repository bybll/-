package 中国象棋;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GameClient extends JFrame {
	
	public ChessBoard chessBoard = null;
	public JLabel lblIp = null;
	public JTextField txtIp = null;
	public JLabel lblPort = null;
	public JTextField txtPort = null;
	public static JButton btnStart = null;
	public static JButton btnGiveup = null;
	public static JButton btnRegret = null;
	
	public GameClient() {
		lblIp = new JLabel("IP地址：");
		txtIp = new JTextField("127.0.0.1");
		lblPort = new JLabel("端口号：");
		txtPort = new JTextField("3003");
		btnStart = new JButton("开始");
		btnGiveup = new JButton("认输");
		btnRegret = new JButton("悔棋");
		chessBoard = new ChessBoard();
		JPanel buttom = new JPanel(new FlowLayout());
		buttom.add(lblIp);
		buttom.add(txtIp);
		buttom.add(lblPort);
		buttom.add(txtPort);
		buttom.add(btnStart);
		buttom.add(btnGiveup);
		buttom.add(btnRegret);
		btnGiveup.setEnabled(false);
		btnRegret.setEnabled(false);
		this.getContentPane().add(chessBoard, BorderLayout.CENTER);
		this.getContentPane().add(buttom, BorderLayout.SOUTH);
		this.setTitle("中国象棋");
		this.setSize(550, 700);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		btnStart.addActionListener(new ActionListener() {//开始按钮

			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = txtIp.getText();
				int otherPort = Integer.parseInt(txtPort.getText());
				int receivePort;
				if(otherPort == 3004) {
					receivePort = 3003;
				} else {
					receivePort = 3004;
				}
				chessBoard.startJoin(ip, receivePort, otherPort);
				btnStart.setEnabled(false);
				btnGiveup.setEnabled(true);
				btnRegret.setEnabled(true);
			}
			
		});
		btnGiveup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chessBoard.send("lose|");
				chessBoard.setEnabled(false);
				btnStart.setEnabled(true);
				btnGiveup.setEnabled(false);
				btnRegret.setEnabled(false);
				chessBoard.flag = false;//终止线程
				JOptionPane.showMessageDialog(chessBoard, "很遗憾，您输了", "中国象棋",JOptionPane.WARNING_MESSAGE);			
			}
			
		});
	}
	
	public static void main(String args[]) {
		new GameClient();
	}
}
