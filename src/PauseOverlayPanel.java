import javax.swing.*;
import java.awt.*;

/**
 * Panel overlay hiện khi pause, dựng trực tiếp lên GamePanel.
 */
public class PauseOverlayPanel extends JPanel {
    public PauseOverlayPanel(GamePanel gamePanel, GameEngine engine) {
        setOpaque(false); // Vẽ bán trong suốt
        setLayout(null);

        int btnWidth = 200, btnHeight = 40;
        int btnSpacing = 15;
        int overlayWidth = gamePanel.getWidth();
        int overlayHeight = gamePanel.getHeight();

        JButton resumeBtn = new JButton("Resume");
        JButton restartBtn = new JButton("Restart");
        JButton saveBtn   = new JButton("Save Game");
        JButton backBtn   = new JButton("Back to Menu");

        resumeBtn.setFont(new Font("Arial", Font.BOLD, 18));
        restartBtn.setFont(new Font("Arial", Font.BOLD, 18));
        saveBtn.setFont(new Font("Arial", Font.BOLD, 18));
        backBtn.setFont(new Font("Arial", Font.BOLD, 18));

        resumeBtn.setBounds((overlayWidth-btnWidth)/2, overlayHeight/2-80, btnWidth, btnHeight);
        restartBtn.setBounds((overlayWidth-btnWidth)/2, overlayHeight/2-80+btnHeight+btnSpacing, btnWidth, btnHeight);
        saveBtn.setBounds((overlayWidth-btnWidth)/2, overlayHeight/2-80+2*(btnHeight+btnSpacing), btnWidth, btnHeight);
        backBtn.setBounds((overlayWidth-btnWidth)/2, overlayHeight/2-80+3*(btnHeight+btnSpacing), btnWidth, btnHeight);

        add(resumeBtn);
        add(restartBtn);
        add(saveBtn);
        add(backBtn);

        // Hành động các nút
        resumeBtn.addActionListener(e -> {
            engine.setPaused(false);
            gamePanel.setPauseOverlay(null);
            gamePanel.requestFocusInWindow();
        });
        restartBtn.addActionListener(e -> {
            engine.restart();
            engine.setPaused(false);
            gamePanel.setPauseOverlay(null);
            gamePanel.requestFocusInWindow();
        });
        saveBtn.addActionListener(e -> {
            boolean ok = SaveManager.quickSave(engine);
            if (ok) {
                JOptionPane.showMessageDialog(gamePanel, "Game đã được lưu thành công!", "Lưu Game", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(gamePanel, "Lưu game thất bại!", "Lưu Game", JOptionPane.ERROR_MESSAGE);
            }
            gamePanel.requestFocusInWindow();
        });
        backBtn.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(gamePanel);
            MainMenuFX.showFXMenu(frame, gamePanel.getWidth(), gamePanel.getHeight());
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Vẽ nền bán trong suốt
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setColor(new Color(0,0,0,180));
        g2.fillRect(0,0,getWidth(),getHeight());

        // Chữ PAUSE
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.setColor(Color.WHITE);
        String text = "Pause";
        int tx = (getWidth()-g2.getFontMetrics().stringWidth(text))/2;
        int ty = getHeight()/2-120;
        g2.drawString(text, tx, ty);

        g2.dispose();
    }
}
