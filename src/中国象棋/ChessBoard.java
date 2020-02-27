package 中国象棋;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ChessBoard extends JPanel implements Runnable{

	public static final short REDPLAYER = 1;
	public static final short BLACKPLAYER = 0;
	
	public boolean flag = false;//进程相关的参数
	
	public Chess[] chess = null;
	public int map[][] = null;
	
	public short localPlayer = REDPLAYER;
	public boolean isMyTurn = true;//轮次
	public boolean isFirstClick = true;//是否第一次点击
	public short player = REDPLAYER;
	public String message = null;
	
	public Image chessBoardImage;
	public Chess firstChess;
	public Chess secondChess;
	public int temp_x, temp_y;
	public int x2,y2,x1,y1;
	public int index1 , index2;
	
	public String ip = "127.0.0.1";
	public int port = 3003, otherport = 3004;
	public void initMap() {
		map = new int[10][9];
		for(int i=0; i<=9; i++) {
			for(int j=0; j<=8; j++) {
				map[i][j] = -1;
			}
		}
	}
	public ChessBoard() {
		chess = new Chess[32];
		this.addMouseListener(new MouseAdapter() {
			 public void mouseClicked(MouseEvent e) {
				 if(isMyTurn == false) {
					 message = "该对方走棋";
					 repaint();
					 return;
				 }
				 selectChess(e);
				 repaint();
				 
			 }
			 public void selectChess(MouseEvent e) {
				 if(isFirstClick) {
					 System.out.println("e.getX():"+e.getX()+","+"e.getY():"+e.getY());
						firstChess = analyse(e.getX(), e.getY());
						
						x1 = temp_x;
						y1 = temp_y;
						
						if(firstChess != null) {
							if(firstChess.player != player) {
								message = "点击对方的棋子";
								return ;
							}
							isFirstClick = false;
						}

					} else {
						secondChess = analyse(e.getX(), e.getY());
						x2 = temp_x;
						y2 = temp_y;
						if(secondChess != null) {
							if(secondChess.player == player) {	//第二次点的棋子是自己的棋子
								firstChess = secondChess;
								secondChess = null;
								repaint();
								return;
							} else if(secondChess.player != player) { //(2).吃棋子
								if(isAbleToMove(firstChess, x2, y2)) {
									index1 = map[x1][y1];
									index2 = map[x2][y2];
									map[x1][y1] = -1;
									map[x2][y2] = index1;
									chess[index1].setPos(x2, y2);
									chess[index2] = null;
									isFirstClick = true;
									SetMyTurn(false);
									send("eat|"+index1+"|"+index2+"|"+(9-x1)+"|"+(8-y1)+"|"+(9-x2)+"|"+(8-y2)+"|");
									repaint();
									return;
								}
								return;
							}
						}
						if(secondChess == null) {
							if(isAbleToMove(firstChess, x2, y2)) {	//(1).移动棋子
								index1 = map[x1][y1];
								map[x1][y1] = -1;
								map[x2][y2] = index1;
								System.out.println("chess[index1]"+chess[index1]);
								chess[index1].setPos(x2, y2);
								repaint();
								send("move|"+index1+"|"+(9-x1)+"|"+(8-y1)+"|"+(9-x2)+"|"+(8-y2)+"|");
								isFirstClick = true;
								SetMyTurn(false);
								return;
							}
						}
					}
				}
			 public Chess analyse(int x, int y) {
				 int index_x = -1;
				 int index_y = -1;
				 int leftX = 10, leftY = 18; 
				 for(int i=0; i<=9; i++) {
						for(int j=0; j<=8; j++) {
							Rectangle r = new Rectangle(leftX+j*55, leftY+i*55, 55, 55);
							if(r.contains(x, y)) {
								index_x = i;
								index_y = j;
							}
						}
					}
				 temp_x = index_x;
				 temp_y = index_y;
				 System.out.println("index_x:"+index_x+","+"index_y:"+index_y);
				 if(index_x==-1 || index_y==-1)  {
					 return null;
				 }
				 if(map[index_x][index_y] == -1) {
					 return null;
				 } else {
					 return  chess[map[index_x][index_y]];
				 }
			 }
			 public boolean isAbleToMove(Chess firstChess, int x2, int y2) {
				String chessType = firstChess.chessType;
				int oldX = firstChess.x;
				int oldY = firstChess.y;
				if(chessType.equals("帅")) {
					if(oldY==y2&&(map[x2][y2]==16||map[x2][y2]==0)) {
						for(int i = x2+1; i < oldX; i++) {
							if(map[i][oldX] != -1) {
								return false;
							}
						}
						return true;
					}
					if(x2<7 || y2<3 || y2>5) {
						return false;
					}//在田字格内
					if(Math.abs(x2-oldX)>=2 || Math.abs(y2-oldY) >= 2) {
						return false;
					}//不超过一格
					if( (x2-oldX) * (y2-oldX) != 0) {
						return false;
					}//不能斜着走
					
					return true;
				} else if(chessType.equals("仕")) {
					if(x2<7 || y2<3 || y2>5) {
						return false;
					}
					if(Math.abs(x2-oldX)>=2 || Math.abs(y2-oldY)>=2) {
						return false;
					}
					if(Math.abs(x2-oldX)==0 || Math.abs(y2-oldY)==0) {
						return false;
					}
					return true;
				} else if(chessType.equals("相")) {
					if(Math.abs(x2-oldX)*Math.abs(y2-oldY)==0) {
						return false;
					}
					if(x2<5) {
						return false;
					}
					if(Math.abs(x2-oldX) != 2 || Math.abs(y2-oldY) != 2) {
						return false;
					}
					int i = 0,j = 0;
					if(x2-oldX == -2) {
						i = oldX - 1;
					}
					if(x2-oldX == 2) {
						i = oldX + 1;
					}
					if(y2 - oldY == -2) {
						j = oldY - 1;
					}
					if(y2 - oldY == 2) {
						j = oldY + 1;
					}
					if(map[i][j] != -1) {
						return false;
					}
					return true;
				} else if(chessType.equals("马")) {
					if(Math.abs(x2-oldX)*Math.abs(y2-oldY) != 2) {
						return false;
					}
					if(x2-oldX == -2) {
						if(map[oldX-1][oldY] != -1) {
							return false;
						}
						return true;
					}
					if(x2-oldX == 2) {
						if(map[oldX+1][oldY] != -1) {
							return false;
						}
						return true;
					}
					if(y2-oldY == -2) {
						if(map[oldX][oldY-1] != -1) {
							return false;
						}
						return true;
					}
					if(y2-oldY == 2) {
						if(map[oldX][oldY+1] != -1) {
							return false;
						}
						return true;
					}
					return true;
				} else if(chessType.equals("车")) {
					if(Math.abs(x2-oldX) != 0 && Math.abs(y2-oldY) != 0 ) {
						return false;
					}
					if(x2-oldX != 0) {
						if(x2-oldX < 0) {
							for(int i = x2+1; i<oldX; i++ ) {
								if(map[i][oldY] != -1) {
									return false;
								}
							}
							return true;
						}
						if(x2-oldX > 0) {
							for(int i = oldX+1; i < x2; i++) {
								if(map[i][oldY] != -1) {
									return false;
								}
							}
							return true;
						}
					}
					if(y2-oldY != 0) {
						if(y2-oldY < 0) {
							for(int i = y2+1; i<oldY; i++ ) {
								if(map[oldX][i] != -1) {
									return false;
								}
							}
							return true;
						}
						if(y2-oldY > 0) {
							for(int i = oldY+1; i < y2; i++) {
								if(map[oldX][i] != -1) {
									return false;
								}
							}
							return true;
						}
					}
					return true;
				} else if(chessType.equals("炮")) {
					if(Math.abs(x2-oldX)*Math.abs(y2-oldY) != 0) {
						return false;
					}
					if(x2-oldX != 0) {
						if(x2-oldX < 0) {
							int j = 0;
							for(int i = x2+1; i < oldX; i++) {
								if(map[i][oldY] != -1) {
									j += 1;
								}
							}
							if(j >1) {
								return false;
							}
							return true;
						}
						if(x2-oldX > 0) {
							int j = 0;
							for(int i = oldX+1; i < x2; i++) {
								if(map[i][oldY] != -1) {
									j += 1;
								}
							}
							if(j >1) {
								return false;
							}
							return true;
						}
					}
					if(y2-oldY != 0) {
						if(y2-oldY < 0) {
							int j = 0;
							for(int i = y2+1; i < oldY; i++) {
								if(map[oldX][i] != -1) {
									j += 1;
								}
							}
							if(j >1) {
								return false;
							}
							return true;
						}
						if(y2-oldY > 0) {
							int j = 0;
							for(int i = oldY+1; i < y2; i++) {
								if(map[oldX][i] != -1) {
									j += 1;
								}
							}
							if(j >1) {
								return false;
							}
							return true;
						}
					}
					return true;
				} else if(chessType.equals("兵")) {
					if(oldX > 4) {
						if(y2-oldY != 0) {
							return false;
						}
						return true;
					}
					if(x2-oldX > 0) {
						return false;
					}
					if(Math.abs(x2-oldX) > 1 || Math.abs(y2-oldY) > 1) {
						return false;
					}
					if(Math.abs(x2-oldX)*Math.abs(y2-oldY) != 0) {
						return false;
					}
		
					return true;
				}
				 return true;
			 }
		});
		
		
	}
	private void SetMyTurn(boolean b) {
		isMyTurn = b;
	}
	public void paint(Graphics g) {
		chessBoardImage = Toolkit.getDefaultToolkit().getImage("D:\\chessImage\\chessBoard.png");
		g.drawImage(chessBoardImage, 0, 0, 520, 590, (ImageObserver)this);
		for(int i = 0; i<32; i++) {
			if(chess[i] != null) {
				chess[i].paint(g, this);
			}
			
		}
		if(firstChess != null) {
			firstChess.drawSelectChess(g);
		}
		if(secondChess != null) {
			secondChess.drawSelectChess(g);
		}
	}
	
	public void initChess() {
		chess[0] = new Chess(BLACKPLAYER,"将", 0, 4);
		map[0][4] = 0;
		chess[1] = new Chess(BLACKPLAYER,"士", 0, 3);
		map[0][3] = 1;
		chess[2] = new Chess(BLACKPLAYER,"士", 0, 5);
		map[0][5] = 2;
		chess[3] = new Chess(BLACKPLAYER,"象", 0, 2);
		map[0][2] = 3;
		chess[4] = new Chess(BLACKPLAYER,"象", 0, 6);
		map[0][6] = 4;
		chess[5] = new Chess(BLACKPLAYER,"马", 0, 1);
		map[0][1] = 5;
		chess[6] = new Chess(BLACKPLAYER,"马", 0, 7);
		map[0][7] = 0;
		chess[7] = new Chess(BLACKPLAYER,"车", 0, 0);
		map[0][0] = 7;
		chess[8] = new Chess(BLACKPLAYER,"车", 0, 8);
		map[0][8] = 8;
		chess[9] = new Chess(BLACKPLAYER,"炮", 2, 1);
		map[2][1] = 9;
		chess[10] = new Chess(BLACKPLAYER,"炮", 2, 7);
		map[2][7] = 10;
		for(int i = 0; i <= 4; i++) {
			chess[11+i] = new Chess(BLACKPLAYER,"卒",3,2*i);
			map[3][2*i] = 11+i;
		}
		
		chess[16] = new Chess(REDPLAYER,"帅", 9, 4);
		map[9][4] =  16;
		chess[17] = new Chess(REDPLAYER,"仕", 9, 3);
		map[9][3] =  17;
		chess[18] = new Chess(REDPLAYER,"仕", 9, 5);
		map[9][5] =  18;
		chess[19] = new Chess(REDPLAYER,"相", 9, 2);
		map[9][2] =  19;
		chess[20] = new Chess(REDPLAYER,"相", 9, 6);
		map[9][6] =  20;
		chess[21] = new Chess(REDPLAYER,"马", 9, 1);
		map[9][1] =  21;
		chess[22] = new Chess(REDPLAYER,"马", 9, 7);
		map[9][7] =  22;
		chess[23] = new Chess(REDPLAYER," 车", 9, 0);
		map[9][0] =  23;		
		chess[24] = new Chess(REDPLAYER," 车", 9, 8);
		map[9][8] =  24;
		chess[25] = new Chess(REDPLAYER,"炮", 7, 1);
		map[7][1] = 25 ;
		chess[26] = new Chess(REDPLAYER,"炮", 7, 7);
		map[7][7] =  26;
		for(int i = 0; i <= 4; i++) {
			chess[i+27] = new Chess(REDPLAYER,"兵", 6, 2*i);
			map[6][2*i] = i+27;
		}
	}
	public void send(String msg) {
		 	DatagramSocket ds = null;
		 	try {
		 		ds = new DatagramSocket();
		 		byte[] btys = msg.getBytes();
		 		InetAddress address = InetAddress.getByName(ip);
		 		DatagramPacket dp = new DatagramPacket(btys, btys.length, address, otherport);
		 		ds.send(dp);
		 	} catch(Exception e) {
		 		e.printStackTrace();
		 	} finally {
		 		if(ds != null) {
		 			ds.close();
		 		}
		 	}
	}
	public void startNewGame(short player) {
		initMap(); //初始化map
		initChess();
		if(player == BLACKPLAYER) {
			reverseBoard();
		}
		repaint();
	}
	public void startJoin(String ip, int receivePort, int otherPort) {
		this.flag = true;
		this.ip = ip;
		this.port = receivePort;
		this.otherport = otherPort;
		send("join|");
		Thread th = new Thread(this);
		th.start();
	}
	public void reverseBoard() {
		for(int i = 0; i<32; i++) {
			if(chess[i] != null) {
				chess[i].reverseChess();
			}
		}
		

		//对两方的棋盘信息进行倒置互换
		for(int i=0;i<5;i++){
			for(int j=0;j<9;j++){
				int temp = map[i][j];
				map[i][j] = map[9-i][8-j];
				map[9-i][8-j] = temp;
			}
		}	
	}
	public void run() {
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(port);
			byte[] data = new byte[1024];
			DatagramPacket dp = new DatagramPacket(data, data.length);
			while(flag==true) {
					ds.receive(dp);
					String message = new String(dp.getData());
					System.out.println(message);	
					String [] array = message.split("\\|");
					if(array[0].equals("join")) { //加入游戏我是红方
						startNewGame(player);
						send("conn|");
						System.out.println(message+"a");
					} else if(array[0].equals("conn")) { //连接至游戏我是黑方
						player = BLACKPLAYER;
						SetMyTurn(false);
						startNewGame(player);
					} else if(array[0].equals("move")) {
						int index1 = Integer.parseInt(array[1]);
						int x1 = Integer.parseInt(array[2]);
						int y1 = Integer.parseInt(array[3]);
						int x2 = Integer.parseInt(array[4]);
						int y2 = Integer.parseInt(array[5]);
						chess[index1].setPos(x2, y2);
						int temp = map[x1][y1];
						map[x1][y1] = -1;
						map[x2][y2] = temp;
						repaint();
						SetMyTurn(true);
					} else if(array[0].equals("eat")) {
						int index1 = Integer.parseInt(array[1]);
						int index2 = Integer.parseInt(array[2]);
						int x1 = Integer.parseInt(array[3]);
						int y1 = Integer.parseInt(array[4]);
						int x2 = Integer.parseInt(array[5]);
						int y2 = Integer.parseInt(array[6]);
						System.out.println("chess[index2]"+chess[index2].chessType);
						if(chess[index2].chessType.equals("将")||chess[index2].chessType.equals("帅")) {
							send("lose|");
							JOptionPane.showMessageDialog(this, "很遗憾，您输了", "中国象棋",JOptionPane.WARNING_MESSAGE);
							GameClient.btnStart.setEnabled(true);
							GameClient.btnGiveup.setEnabled(false);
							GameClient.btnRegret.setEnabled(false);
							this.setEnabled(false);
						}
						chess[index1].setPos(x2, y2);
						chess[index2] = null;
						int temp = map[x1][y1];
						map[x1][y1] = -1;
						map[x2][y2] = temp;
						repaint();
						SetMyTurn(true);
					} else if(array[0].equals("lose")) {
						JOptionPane.showMessageDialog(this, "恭喜您，您赢了", "中国象棋",JOptionPane.WARNING_MESSAGE);
						GameClient.btnStart.setEnabled(true);
						GameClient.btnGiveup.setEnabled(false);
						GameClient.btnRegret.setEnabled(false);
						this.setEnabled(false);
					}
				} 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(ds != null) {
					ds.close();
				}
			}
		}
}
