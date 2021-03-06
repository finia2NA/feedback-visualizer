package View;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

import Main.Answer;
import Main.MyUtil;
import acm.program.GraphicsProgram;
import acm.program.Program;

public class MainView {
  GraphicsProgram top;
  ScrollingPanel[] SPs;
  boolean MLPs = false;
  boolean DEBUG = false;
  private ScrollingPanel activePanel;
  private ListPanel focus = null;
  private static int nMultiListpanels = 2;
  private static double SCROLLAMOUNT = 50;

  public MainView(GraphicsProgram top, int numberOfAreas) {
    commonCore(top, numberOfAreas);
  }

  public MainView(GraphicsProgram top, boolean MLPs, int numberOfAreas) {
    this.MLPs = MLPs;
    commonCore(top, numberOfAreas);
  }

  public MainView(GraphicsProgram top, boolean MLPs, int numberOfAreas, boolean DEBUG) {
    this.MLPs = MLPs;
    this.DEBUG = DEBUG;
    commonCore(top, numberOfAreas);
  }

  public MainView(GraphicsProgram top, int numberOfAreas, boolean DEBUG) {
    this.DEBUG = DEBUG;
    commonCore(top, numberOfAreas);
  }

  /**
   * common code to all the contructors. generates buttons and sets active panel.
   * 
   * @param top
   * @param numberOfAreas
   */
  private void commonCore(GraphicsProgram top, int numberOfAreas) {
    this.top = top;

    if (MLPs) {
      // +1 weil die liste 0 alle antworten zeigt.
      SPs = new MultiListPanel[numberOfAreas + 1];
      for (int i = 0; i < numberOfAreas + 1; i++) {
        SPs[i] = new MultiListPanel(nMultiListpanels);
        top.add(SPs[i]);
        SPs[i].setVisible(false);
        top.add(new JButton("" + i), Program.EAST);
      }
    } else {
      // +1 weil die liste 0 alle antworten zeigt.
      SPs = new ListPanel[numberOfAreas + 1];
      for (int i = 0; i < numberOfAreas + 1; i++) {
        SPs[i] = new ListPanel(DEBUG);
        top.add(SPs[i]);
        SPs[i].setVisible(false);
        top.add(new JButton("" + i), Program.EAST);
      }
    }
    top.add(new JButton("Up"), Program.EAST);
    top.add(new JButton("Down"), Program.EAST);
    top.add(new JButton("Reset"), Program.EAST);

    SPs[0].setVisible(true);
    activePanel = SPs[0];
  }

  /**
   * adds a Speechbubble representing the answer to all lists that apply.
   * 
   * @param a
   */
  public void add(Answer a) {
    int[] toAdds = a.getZuordnungen();
    if (toAdds == null) {
      System.err.println("Please init ZUORDNUNGEN b4 adding to MainView");
      return;
    }
    SPs[0].add(a);
    if (toAdds.length == 0)
      return;

    for (int i : toAdds) {
      SPs[i].add(a);
    }
  }

  public void showPanel(int panel) {
    if(focus!=null) {
      top.remove(focus);
      focus = null;
      activePanel.setVisible(true);
    }
    assert panel <= SPs.length;
    for (ScrollingPanel lp : SPs)
      lp.setVisible(false);

    SPs[panel].setVisible(true);
    activePanel = SPs[panel];
  }

  public void mouseClicked(MouseEvent e) {
    if (focus == null) {
      SpeechBubble activeSP = activePanel.getSBat(e.getX(), e.getY());
      if (activeSP == null)
        return;
      activePanel.setVisible(false);
      focus = new ListPanel(DEBUG);
      focus.add(new Answer(activeSP.name, activeSP.message));
      top.add(focus);
    } else {
      top.remove(focus);
      focus = null;
      activePanel.setVisible(true);
    }
  }

  public void actionPerformed(ActionEvent e) {
    String label = e.getActionCommand();
    // 3 scrolling cases
    if (label.equals("Down")) {
      activePanel.scroll(SCROLLAMOUNT);
    } else if (label.equals("Up")) {
      activePanel.scroll(-SCROLLAMOUNT);

    } else if (label.equals("Reset")) {
      activePanel.resetScroll();
      ;
      // Select cases
    } else {
      if (!MyUtil.isIntegerString(label))
        return;
      int identifier = Integer.parseInt(label);
      showPanel(identifier);
    }
  }

}
