/*
 * SymbolicLinkTranscoderImpl.java
 * 
 * Created: Jun 21, 2010
 * 
 * Copyright (C) 2010 Scott Kidder
 * 
 * This file is part of mythpodcaster
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package net.urlgrey.mythpodcaster.transcode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import net.urlgrey.mythpodcaster.xml.GenericTranscoderConfigurationItem;

/**
 * @author scottkidder
 * 
 */
public class SymbolicLinkTranscoderImpl extends AbstractTranscoderImpl implements Transcoder {

  private static final Logger LOG = Logger.getLogger(SymbolicLinkTranscoderImpl.class);

  static final ExecutorService pool = Executors.newCachedThreadPool();

  /*
   * (non-Javadoc)
   * 
   * @see net.urlgrey.mythpodcaster.transcode.Transcoder#transcode(java.io.File,
   * net.urlgrey.mythpodcaster.dto.GenericTranscoderConfigurationItem, java.io.File, java.io.File)
   */
  @Override
  public void transcode(File workingDirectory, GenericTranscoderConfigurationItem config,
      File inputFile, File outputFile) throws Exception {

    LOG.info("transcode started: inputFile [" + inputFile.getAbsolutePath() + "], outputFile ["
        + outputFile.getAbsolutePath() + "]");

    List<String> commandList = new ArrayList<String>();
    commandList.add("ln");
    commandList.add("-s");
    commandList.add(inputFile.getAbsolutePath());
    commandList.add(outputFile.getAbsolutePath());
    ProcessBuilder pb = new ProcessBuilder(commandList);

    pb.environment().put("LD_LIBRARY_PATH", "/usr/local/lib:");
    pb.redirectErrorStream(true);
    pb.directory(outputFile.getParentFile());
    Process process = null;

    try {
      // Get the segmenter process
      process = pb.start();
      // We give a couple of secs to complete task if needed
      Future<List<String>> stdout = pool.submit(new OutputMonitor(process.getInputStream()));
      List<String> result = stdout.get(config.getTimeout(), TimeUnit.SECONDS);
      process.waitFor();
      final int exitValue = process.exitValue();
      LOG.info("Link creation exit value: " + exitValue);
      if (exitValue != 0) {
        for (String line : result) {
          LOG.error(line);
        }
        throw new Exception("Link return code indicated failure: " + exitValue);
      }
    } catch (InterruptedException e) {
      throw new Exception("Link process interrupted by another thread", e);
    } catch (ExecutionException ee) {
      throw new Exception("Something went wrong parsing Link output", ee);
    } catch (TimeoutException te) {
      // We could not get the result before timeout
      throw new Exception("Link process timed out", te);
    } catch (RuntimeException re) {
      // Unexpected output from Link
      throw new Exception("Something went wrong parsing Link output", re);
    } finally {
      if (process != null) {
        process.destroy();
      }
    }

    LOG.debug("transcoding finished");
  }

}
