package org.freehep.jas.extensions.jconsole;

import com.wittams.gritty.Questioner;
import com.wittams.gritty.RequestOrigin;
import com.wittams.gritty.Tty;
import com.wittams.gritty.swing.GrittyTerminal;
import com.wittams.gritty.swing.TermPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import jline.Terminal;
import jline.console.ConsoleReader;

public class JLineConsole implements JConsole {
  
// -- Private parts : ----------------------------------------------------------

  private final GrittyTerminal _terminal;
  private final ConsoleReader _reader;

    
// -- Construction and life cycle : --------------------------------------------

  public JLineConsole(String name) throws IOException {
    
//    _panel = new JPanel();
//    _panel.setName(name);
    
    _terminal = new GrittyTerminal();
    _terminal.setName(name);
    TermPanel termPanel = _terminal.getTermPanel();
//    _panel.add(termPanel);
    termPanel.setResizePanelDelegate((final Dimension pixelDimension, final RequestOrigin origin) -> {
      if (origin == RequestOrigin.Remote) {
//        _panel.setSize(pixelDimension);
//        _panel.revalidate();
        _terminal.setSize(pixelDimension);
        _terminal.revalidate();
      }
    });
    termPanel.setSize(termPanel.getPreferredSize());
    
    TtyImpl tty = new TtyImpl();
    _terminal.setTty(tty);
    
    _reader = new ConsoleReader(tty.getInputStream(), tty.getOutputStream(), new SwingTerminal());
    _terminal.start();
  }

  @Override
  public void dispose() {
    _terminal.stop();
  }


// -- Getters : ----------------------------------------------------------------
    
  @Override    
  public String getName() {
    return _terminal.getName();
  }
  
  @Override    
  public Component getView() {
    return _terminal;
  }

  public ConsoleReader getConsoleReader() {
    return _reader;
  }

  @Override
  public int getVerticalScrollBarPolicy() {
    return ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
  }

  @Override
  public int getHorizontalScrollBarPolicy() {
    return ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
  }


// -- Local classes : ----------------------------------------------------------

  private class SwingTerminal implements Terminal {

    @Override
    public void init() throws Exception {
    }

    @Override
    public void restore() throws Exception {
    }

    @Override
    public void reset() throws Exception {
    }

    @Override
    public boolean isSupported() {
      return true;
    }

    @Override
    public int getWidth() {
      //FIXME: should be calculated based on size of window
      return 80;
    }

    @Override
    public int getHeight() {
      //FIXME: should be calculated based on size of window
      return 100;
    }

    @Override
    public boolean isAnsiSupported() {
      return false;
    }

    @Override
    public OutputStream wrapOutIfNeeded(OutputStream out) {
      return out;
    }

    @Override
    public InputStream wrapInIfNeeded(InputStream in) throws IOException {
      return in;
    }

    @Override
    public boolean hasWeirdWrap() {
      return false;
    }

    @Override
    public boolean isEchoEnabled() {
      return false;
    }

    @Override
    public void setEchoEnabled(boolean bln) {
    }
  }

  private class TtyImpl implements Tty {

    private final BlockingQueue<byte[]> inputQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<byte[]> outputQueue = new LinkedBlockingQueue<>();
    private final TtyInputStream inputStream;
    private final TtyOutputStream outputStream;

    public TtyImpl() {
      inputStream = new TtyInputStream();
      outputStream = new TtyOutputStream();
    }

    @Override
    public boolean init(Questioner qstnr) {
      return true;
    }

    @Override
    public void close() {
    }

    @Override
    public void resize(Dimension dmnsn, Dimension dmnsn1) {
    }

    @Override
    public String getName() {
      return "Test";
    }

    @Override
    public int read(byte[] bytes, int i, int i1) throws IOException {
      try {
        byte[] src = outputQueue.take();
        // FIXME: need to handle case where src does not fit in target
        System.arraycopy(src, 0, bytes, i, src.length);
        return src.length;
      } catch (InterruptedException ex) {
        throw new InterruptedIOException();
      }
    }

    @Override
    public void write(byte[] bytes) throws IOException {
      try {
        inputQueue.put(bytes);
      } catch (InterruptedException ex) {
        throw new InterruptedIOException();
      }
    }

    public TtyInputStream getInputStream() {
      return inputStream;
    }

    public TtyOutputStream getOutputStream() {
      return outputStream;
    }

    private class TtyInputStream extends InputStream {

      private byte[] currentBuffer;
      private int pos;

      @Override
      public int read() throws IOException {
        try {
          while (currentBuffer == null || currentBuffer.length <= pos) {
            currentBuffer = inputQueue.take();
            pos = 0;
          }
          return currentBuffer[pos++];
        } catch (InterruptedException interruptedException) {
          throw new InterruptedIOException();
        }
      }

      @Override
      public int read(byte[] b, int off, int len) throws IOException {
        try {
          if (currentBuffer == null || currentBuffer.length <= pos) {
            currentBuffer = inputQueue.take();
            pos = 0;
          }
          int actual = Math.min(len, currentBuffer.length - pos);
          System.arraycopy(currentBuffer, pos, b, off, actual);
          pos += actual;
          return actual;
        } catch (InterruptedException interruptedException) {
          throw new InterruptedIOException();
        }
      }
    }

    private class TtyOutputStream extends OutputStream {

      @Override
      public void write(int b) throws IOException {
        try {
          byte[] buffer = new byte[1];
          buffer[0] = (byte) b;
          outputQueue.put(buffer);
          if (b == 0xa) {
            byte[] buffer2 = new byte[1];
            buffer2[0] = 0xd;
            outputQueue.put(buffer2);
          }
        } catch (InterruptedException interruptedException) {
          throw new InterruptedIOException();
        }
      }
    }
  }

}
