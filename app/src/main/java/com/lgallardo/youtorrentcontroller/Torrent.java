/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * Luis M. Gallardo D. - initial implementation
 ******************************************************************************/
package com.lgallardo.youtorrentcontroller;

import android.util.Log;

class Torrent {

    private String file;
    private String size;
    private String info;
    private String state;
    private String hash;
    private String downloadSpeed;
    private String uploadSpeed;
    private String ratio;
    private String progress;
    private String downloaded;
    private int peersConnected;
    private int peersInSwarm;
    private int seedsConnected;
    private int seedInSwarm;
    private String priority;
    private String eta;
    private String status;
    private String label;
    private String availability;
    private boolean completed;

    private String savePath;
    private String creationDate;
    private String comment;
    private String totalWasted;
    private String totalUploaded;
    private String totalDownloaded;
    private String timeElapsed;
    private String nbConnections;
    private String uploadLimit;
    private String downloadLimit;
    private boolean sequentialDownload;
    private boolean firstLastPiecePrio;

    public Torrent(String file, String size, String state, String hash,
                   String info, String ratio, String progress, int peersConnected, int peersInSwarm,
                   int seedsConnected, int seedInSwarm, String priority, String eta, String downloadSpeed,
                   String uploadSpeed, boolean sequentialDownload, boolean firstLastPiecePrio, String status,
                   String label, String availability, boolean completed) {
        this.file = file;
        this.size = size;
        this.state = state;
        this.hash = hash;
        this.info = info;
        this.ratio = ratio;
        this.progress = progress;
        this.peersConnected = peersConnected;
        this.peersInSwarm = peersInSwarm;
        this.seedsConnected = seedsConnected;
        this.seedInSwarm = seedInSwarm;
        this.priority = priority;
        this.eta = eta;
        this.downloadSpeed = downloadSpeed;
        this.uploadSpeed = uploadSpeed;
        this.sequentialDownload = sequentialDownload;
        this.firstLastPiecePrio = firstLastPiecePrio;
        this.status = status;
        this.label = label;
        this.availability = availability;
        this.completed = completed;

    }

    /**
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the downloadSpeed
     */
    public String getDownloadSpeed() {
        return downloadSpeed;
    }

    /**
     * @param downloadSpeed the downloadSpeed to set
     */
    public void setDownloadSpeed(String downloadSpeed) {
        this.downloadSpeed = downloadSpeed;
    }

    public int getSpeedWeight(String type) {

        String scalar = "0";
        String unit = "";
        int weight = 0;

        String speed = this.downloadSpeed;

        if (type.equals("upload")) {
            speed = this.uploadSpeed;
        }


        String[] words = speed.split("\\s+");

        if (words.length == 2) {

            try {
                scalar = words[0].replace(",", ".");
            } catch (Exception e) {
            }

            try {
                unit = words[1];
            } catch (Exception e) {
            }

            weight = (int) (Float.parseFloat(scalar) * 10);

            if (unit.charAt(0) == 'B') {
                weight = (int) (Float.parseFloat(scalar) / 10);
            }
            if (unit.charAt(0) == 'K') {
                weight = weight * 10;
            }

            if (unit.charAt(0) == 'M') {
                weight = weight * 100;
            }

            if (unit.charAt(0) == 'G') {
                weight = weight * 1000;
            }

            if (unit.charAt(0) == 'T') {
                weight = weight * 10000;
            }

        }

        return weight;
    }

    public int getDownloadSpeedWeight() {
        return getSpeedWeight("download");
    }

    /**
     * @return the uploadSpeed
     */
    public String getUploadSpeed() {
        return uploadSpeed;
    }

    public int getUploadSpeedWeight() {
        return getSpeedWeight("upload");
    }

    /**
     * @param uploadSpeed the uploadSpeed to set
     */
    public void setUploadSpeed(String uploadSpeed) {
        this.uploadSpeed = uploadSpeed;
    }

    /**
     * @return the ratio
     */
    public String getRatio() {
        return ratio;
    }

    /**
     * @param ratio the ratio to set
     */
    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    /**
     * @return the progress
     */
    public String getProgress() {
        return progress;
    }

    /**
     * @param progress the progress to set
     */
    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getPercentage() {

        int index = progress.indexOf(".");

        if (index == -1) {
            index = progress.indexOf(",");

            if (index == -1) {
                index = progress.length();
            }
        }

        return progress.substring(0, index);

    }

    public int getEtaInMinutes() {

        String[] words = eta.split("\\s+");
        String minutes = "0";
        String hours = "0";
        String days = "0";
        int etaInMinutes = 0;

        if (words.length == 1) {

            if (words[0].equals("∞")) {
                minutes = "144000";
            } else {
                if (words[0].equals("0")) {
                    minutes = "0";
                } else {

                    minutes = words[0].substring(0, words[0].length() - 1);

                }
            }


        } else {

            if (words[0].substring(words[0].length() - 1, words[0].length()).equals("d")) {

                days = words[0].substring(0, words[0].length() - 1);
                hours = words[1].substring(0, words[1].length() - 1);

            } else {

                hours = words[0].substring(0, words[0].length() - 1);
                minutes = words[1].substring(0, words[1].length() - 1);

            }

        }

        try {
            etaInMinutes = Integer.parseInt(days) * 1440 + Integer.parseInt(hours) * 60 + Integer.parseInt(minutes);
        } catch (Exception e) {
            etaInMinutes = 144000;
            Log.e("Torrent", e.toString());
        }

        return etaInMinutes;
    }

    /**
     * @return the downloaded size
     */
    public String getDownloaded() {
        return downloaded;
    }

    /**
     * @param downloaded the downloaded to set
     */
    public void setDownloaded(String downloaded) {
        this.downloaded = downloaded;
    }


    /**
     * @return the priority
     */
    public String getPriority() {

        if (priority.equals("-1")) {
            return "*";
        }
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * @return the eta
     */
    public String getEta() {

        return eta;
    }

    /**
     * @param eta the eta to set
     */
    public void setEta(String eta) {
        this.eta = eta;
    }

    /**
     * @return the path
     */
    public String getSavePath() {
        return savePath;
    }

    /**
     * @param savePath the path to set
     */
    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    /**
     * @return the creationDate
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the totalWasted
     */
    public String getTotalWasted() {
        return totalWasted;
    }

    /**
     * @param totalWasted the totalWasted to set
     */
    public void setTotalWasted(String totalWasted) {
        this.totalWasted = totalWasted;
    }

    /**
     * @return the totalUploaded
     */
    public String getTotalUploaded() {
        return totalUploaded;
    }

    /**
     * @param totalUploaded the totalUploaded to set
     */
    public void setTotalUploaded(String totalUploaded) {
        this.totalUploaded = totalUploaded;
    }

    /**
     * @return the totalDownloaded
     */
    public String getTotalDownloaded() {
        return totalDownloaded;
    }

    /**
     * @param totalDownloaded the totalDownloaded to set
     */
    public void setTotalDownloaded(String totalDownloaded) {
        this.totalDownloaded = totalDownloaded;
    }

    /**
     * @return the timeElapsed
     */
    public String getTimeElapsed() {

        return timeElapsed;
    }

    /**
     * @param timeElapsed the timeElapsed to set
     */
    public void setTimeElapsed(String timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    /**
     * @return the nbConnections
     */
    public String getNbConnections() {
        return nbConnections;
    }

    /**
     * @param nbConnections the nbConnections to set
     */
    public void setNbConnections(String nbConnections) {
        this.nbConnections = nbConnections;
    }

    public String getUploadLimit() {

        if (uploadLimit.equals("-1")) {
            return "∞";
        }

        return uploadLimit;
    }

    public void setUploadLimit(String uploadLimit) {
        this.uploadLimit = uploadLimit;
    }

    public String getDownloadLimit() {

        if (downloadLimit.equals("-1")) {
            return "∞";
        }

        return downloadLimit;
    }

    public void setDownloadLimit(String downloadLimit) {
        this.downloadLimit = downloadLimit;
    }


    public boolean getSequentialDownload() {
        return sequentialDownload;
    }

    public void setSequentialDownload(boolean sequentialDownload) {
        this.sequentialDownload = sequentialDownload;
    }

    public boolean getisFirstLastPiecePrio() {
        return firstLastPiecePrio;
    }

    public void setFirstLastPiecePrio(boolean firstLastPiecePrio) {
        this.firstLastPiecePrio = firstLastPiecePrio;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSeedInSwarm() {
        return seedInSwarm;
    }

    public void setSeedInSwarm(int seedInSwarm) {
        this.seedInSwarm = seedInSwarm;
    }

    public int getSeedsConnected() {
        return seedsConnected;
    }

    public void setSeedsConnected(int seedsConnected) {
        this.seedsConnected = seedsConnected;
    }

    public int getPeersInSwarm() {
        return peersInSwarm;
    }

    public void setPeersInSwarm(int peersInSwarm) {
        this.peersInSwarm = peersInSwarm;
    }

    public int getPeersConnected() {
        return peersConnected;
    }

    public void setPeersConnected(int peersConnected) {
        this.peersConnected = peersConnected;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
