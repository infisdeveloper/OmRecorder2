package omrecorder2;

import java.io.File;

/**
 * {@code Pcm} is recorder for recording audio in wav format.
 *
 * @author Kailash Dabhi
 * @date 31-07-2016
 */
final class Pcm extends AbstractRecorder {
  public Pcm(PullTransport pullTransport, File file) {
    super(pullTransport, file);
  }
}