package 中国象棋;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;

import javax.swing.JPanel;

public class Chess {
		
	public static final short REDPLAYER = 1;
	public static final short BLACKPLAYER = 0;
	public short player;
	public String chessType;
	public int x,y;
	public Image chessImage;
	public int leftX = 10, leftY = 18; 
	public Chess(short player, String chessType, int x, int y) {
		this.player = player;
		this.chessType = chessType;
		this.x = x; 
		this.y = y;		 //做初始化
		
		if(player == REDPLAYER) {
			
			switch(chessType) {
				
			case "帅":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess7.png");
				break;	
			case "仕":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess8.png");
				break;	
			case "相":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess9.png");
				break;	
			case "马":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess10.png");
				break;	
			case "车":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess11.png");
				break;
			case "炮":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess12.png");
				break;
			case "兵":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess13.png");
				break;	
			}
			
		} else {
			
			switch(chessType) {
			
			case "将":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess0.png");
				break;	
			case "士":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess1.png");
				break;	
			case "象":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess2.png");
				break;	
			case "马":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess3.png");
				break;	
			case "车":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess4.png");
				break;
			case "炮":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess5.png");
				break;
			case "卒":
				chessImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chess6.png");
				break;
				
		}
	}
	
}
	public void setPos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void paint(Graphics g, JPanel panel) {
			
		g.drawImage(chessImage, leftX+y*55, leftY+x*55, 55, 55, (ImageObserver) panel);
		
	}
	
	public void reverseChess() {
		this.x = 9-x;
		this.y = 8-y;
	}
	public void drawSelectChess(Graphics g) {
		g.drawRect( leftX+y*55, leftY+x*55, 55, 55);
	}
}
