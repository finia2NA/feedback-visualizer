package Main;

import java.awt.event.MouseEvent;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import View.MainView;
import WatchSystem.WatchThread;
import acm.program.GraphicsProgram;

@SuppressWarnings("serial")
public class Main extends GraphicsProgram {
  final static boolean DEBUG = false;
  final static Path path = Paths.get("O:\\Studium\\Kompetenz\\feedback-server\\Node\\data");
  int howManyCategories = 5;

  int phase = 0;
  CopyOnWriteArrayList<Answer> AnswerList;

  MainView view;
  WatchThread watcher;

  public void init() {
    AnswerList = new CopyOnWriteArrayList<Answer>();
    ;
    try {
      println("Expecting data in: " + path);

      printIP();
      addMouseListeners();
    } catch (Exception e) {
      e.printStackTrace();
    }
    view = new MainView(this, howManyCategories, false);
    // readLine("Press Enter to start");
  }

  public void run() {
    WatchThread watcher = new WatchThread();
    this.watcher = watcher;
    watcher.init(path, AnswerList, DEBUG);
    watcher.start();
    int processed = 0;
    while (true) {
      if (processed < AnswerList.size()) {
        processAnswer(processed);
        processed++;
      }
      if (phase > 0)
        break;
    }

  }

  private void processAnswer(int numAnswer) {
    String s = readLine(AnswerList.get(numAnswer).toString());
    String[] split = s.split("\\s+");
    ArrayList<Integer> zuordnungen = new ArrayList<Integer>();
    for (int i = 0; i < split.length; i++) {
      //TODO: deal with the case that the user inputs something that is not an int
      int zahl = Integer.parseInt(split[i]);
      if (zahl > howManyCategories) {
        System.err.println("please don't give me any numbers over howManyCategories.");
      } else {
        zuordnungen.add(zahl);
      }
    }
    // convert to array
    int[] atm = new int[zuordnungen.size()];
    for (int i = 0; i < atm.length; i++) {
      atm[i] = zuordnungen.get(i);
    }

    AnswerList.get(numAnswer).setZuordnung(atm);
    view.add(AnswerList.get(numAnswer));
  }

  private void printIP() throws SocketException, UnknownHostException {
    DatagramSocket socket;
    socket = new DatagramSocket();
    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
    String ip = socket.getLocalAddress().getHostAddress();
    println("Local IP is " + ip);
    socket.close();
  }

  public void mouseClicked(MouseEvent e) {

  }
}
