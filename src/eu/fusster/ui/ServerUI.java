package eu.fusster.ui;

import static eu.fusster.toolkit.Utils.*;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.*;

import eu.fusster.game.CommandExecutor;
import eu.fusster.player.Player;
import eu.fusster.toolkit.Utils;

@SuppressWarnings("rawtypes")
public class ServerUI extends JFrame {
	private static final long serialVersionUID = 1L;

	private JButton executeButton;
	private JScrollPane jScrollPane1;
	private JScrollPane jScrollPane2;
	private JList usernamePane;
	private JTextPane consolePane;
	private JTextField commandLine;
	private JCheckBox debugCheckBox;

	public int PORT = 9876;

	JPopupMenu pm;

	ServerSocket socket;
	Socket connectionSocket = null;

	public ServerUI() {
		initComponents();
		Runtime.getRuntime().gc();
	}

	private void onButtonClick() {
		CommandExecutor.execute(CommandExecutor.SERVER_COMMAND, commandLine
				.getText().trim(), null);
		commandLine.setText("");
	}

	private void initComponents() {

		setTitle("Fusster : Server");
		setResizable(false);

		debugCheckBox = new JCheckBox();
		setUsernamePane(new JList());
		setConsoleArea(new JTextPane());
		jScrollPane1 = new JScrollPane();
		jScrollPane2 = new JScrollPane();
		commandLine = new JTextField();
		executeButton = new JButton();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setMaximumSize(new Dimension(860, 640));
		setMinimumSize(new Dimension(860, 640));
		setPreferredSize(new Dimension(860, 640));

		debugCheckBox.setText("Debuging");
		debugCheckBox.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setDebbuging(!isDebbuging());
				if (isDebbuging()) {
					append("Debugging is turned on.", Color.GREEN);
				} else {
					append("Debugging is turned off.", Color.RED);
				}
			}
		});

		jScrollPane1.setViewportView(getUsernamePane());

		getUsernamePane().addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					
					if (Utils.getPlayers().size() >= 1){
						
						JList list = (JList) e.getSource();
						final int row = list.locationToIndex(e.getPoint());
						list.setSelectedIndex(row);

						pm = new JPopupMenu();

						JMenuItem mi = new JMenuItem("Kick");
						JMenuItem mi2 = new JMenuItem("Stats");
						JMenuItem mi3 = new JMenuItem("Message");

						mi.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Utils.removePlayer(getPlayer(row),
										"Kicked by Console");
							}
						});

						mi2.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Player p = getPlayer(row);
								final JFrame f = new JFrame("Stats for "
										+ p.getName());

								String alive = "Yes";
								if (p.isDead())
									alive = "No";

								boolean inGame = p.isInGame();

								Object rowData[][];

								if (inGame) {
									Object rowDataA[][] = {
											{ "Name", p.getName() },
											{ "IP", p.getSocket().getInetAddress() },
											{ "InGame", "No" }, { "Alive", alive }, };
									rowData = rowDataA;
								} else {
									Object rowDataA[][] = {
											{ "Name", p.getName() },
											{ "IP", p.getSocket().getInetAddress() },
											{ "Ammo", "< Not in Game >" },
											{ "Alive", "< Not in Game >" },
											{ "Killed", p.pplKilled().toString() },
											{ "Total Times Shot", "< Not in Game >" },
											{ "Total Times Defended",
													"< Not in Game >" },
											{ "Total Times Reloaded",
													"< Not in Game >" },
											{ "Total Times Reloaded",
													"< Not in Game >" }, };
									rowData = rowDataA;
								}

								Object columnNames[] = { "Name", "Value" };

								JTable table = new JTable(rowData, columnNames);
								JScrollPane scrollPane = new JScrollPane(table);

								Button b = new Button("Close");

								b.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent e) {
										f.dispose();
									}
								});

								f.add(scrollPane, BorderLayout.CENTER);
								f.add(b, BorderLayout.SOUTH);
								f.setSize(300, 200);
								f.setLocationRelativeTo(null);
								f.setUndecorated(false);
								f.setResizable(false);
								f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
								f.setVisible(true);
							}
						});

						mi3.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Player p = getPlayer(row);
								String toSend = JOptionPane.showInputDialog(null,
										"Send Message",
										"Send a message to " + p.getName(),
										JOptionPane.INFORMATION_MESSAGE);
								if (toSend != null)
									Utils.send(p, "msg:" + toSend);
							}
						});

						pm.add(mi);
						pm.add(mi2);
						pm.add(mi3);
						list.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent e) {
								pm.show(e.getComponent(), e.getX(), e.getY());
							}
						});
					}
				}
			}
		});

		getConsoleArea().setEditable(false);
		jScrollPane2.setViewportView(getConsoleArea());

		executeButton.setText("Execute");

		executeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onButtonClick();
			}

		});

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jScrollPane1,
										GroupLayout.PREFERRED_SIZE, 190,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.LEADING)
												.addComponent(
														jScrollPane2,
														GroupLayout.DEFAULT_SIZE,
														535, Short.MAX_VALUE)
												.addComponent(commandLine)
												.addGroup(
														GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		debugCheckBox)
																.addGap(0,
																		0,
																		Short.MAX_VALUE)
																.addComponent(
																		executeButton)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.LEADING)
												.addComponent(jScrollPane1)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		jScrollPane2,
																		GroupLayout.PREFERRED_SIZE,
																		474,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(18, 18,
																		18)
																.addComponent(
																		commandLine,
																		GroupLayout.PREFERRED_SIZE,
																		35,
																		GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		LayoutStyle.ComponentPlacement.RELATED,
																		12,
																		Short.MAX_VALUE)
																.addComponent(
																		debugCheckBox)
																.addComponent(
																		executeButton)))
								.addContainerGap()));

		pack();
		setVisible(true);
	}

	public JList getUsernamePane() {
		return usernamePane;
	}

	public void setUsernamePane(JList usernameArea) {
		usernamePane = usernameArea;
	}

	public JTextPane getConsoleArea() {
		return consolePane;
	}

	public void setConsoleArea(JTextPane consoleArea) {
		consolePane = consoleArea;
	}

}