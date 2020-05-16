package com.kaadas.lock.bean;

import java.util.List;

public class GateWay6032Result {


    /**
     * msg : 成功
     * code : 200
     * data : {"headersIp":"116.24.67.128","shareFlag":0,"weekSchedule":[{"endHour":14,"daysMask":127,"startHour":13,"startMinutes":42,"scheduleStatus":"1","updateTime":1588225480102,"endMinutes":42,"userId":10,"scheduleId":10}],"device_type":"kdszblock","yearSchedule":[{"scheduleStatus":"1","startTime":641225076,"updateTime":1588225322702,"endTime":641311476,"userId":0,"scheduleId":0},{"scheduleStatus":"1","startTime":641499211,"updateTime":1588225332007,"endTime":641585611,"userId":3,"scheduleId":3},{"scheduleStatus":"1","startTime":641499367,"updateTime":1588225335197,"endTime":641585767,"userId":4,"scheduleId":4}],"deviceId":"Z102201410045","lockversion":"8100Z;00;V1.02.040;V0.00.000","pwdList":[{"userStatus":"1","updateTime":1588138739505,"userType":1,"pwd":"","userId":0},{"userStatus":"1","updateTime":1588138743218,"userType":0,"pwd":"","userId":2},{"userStatus":"1","updateTime":1588138740930,"userType":0,"pwd":"","userId":1}],"nwaddr":9718,"moduletype":"KSZG1703U","eventTime":"2020-04-30 13:41:57.426","shortAddr":10000,"onlineTime":"2020-04-30 13:41:57.426","model":"8100Z","offlineTime":"2020-04-30 13:13:28.359","endpointList":[{"endpoint":10,"outputClusters":{"doorLockInfo":{"lockInfo":{"numberOfWeekDaySchedulesSupportedPerUser":220,"minRFIDCodeLength":4,"maxPINCodeLength":12,"minPINCodeLength":6,"numberOfPINUsersSupported":20,"numberOfRFIDUsersSupported":100,"updateTime":1588225572947,"numberOfYearDaySchedulesSupportedPerUser":220,"numberOfTotalUsersSupported":0,"maxRFIDCodeLength":10},"operationalSettings":{"soundVolume":2,"updateTime":1588225306881,"language":"zh"},"basicInfo":{"updateTime":1588225317368,"lockState":"lock"}},"powerInfo":{"batteryOneInfo":{"percentageRemaining":0,"updateTime":1588225299595,"voltage":0}},"basicInfo":{"manufacturerVersionDesc":"8100Z;00;V1.02.040;V0.00.000","hardwareVersion":16,"softwareInfo":"V1.04.004","model":"Z102201410045","updateTime":1588225296203}}}],"ipaddr":"","SW":"V1.04.004","moduleType":"","delectTime":"2020-04-30 13:40:08.256","nickName":"姐姐","event_str":"online","updateTime":1588225296287,"nodeType":1,"macaddr":"00:12:4b:00:21:61:e0:63","endpointCount":10,"time":"2020-04-30 10:24:29.659","_id":"5ea54e534d27d6da127dcd9c","zigbeeIEEE":"00:12:4b:00:21:61:e0:63"}
     * nowTime : 1588226741
     */
    private String msg;
    private String code;
    private DataEntity data;
    private int nowTime;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setNowTime(int nowTime) {
        this.nowTime = nowTime;
    }

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    public DataEntity getData() {
        return data;
    }

    public int getNowTime() {
        return nowTime;
    }

    public class DataEntity {
        /**
         * headersIp : 116.24.67.128
         * shareFlag : 0
         * weekSchedule : [{"endHour":14,"daysMask":127,"startHour":13,"startMinutes":42,"scheduleStatus":"1","updateTime":1588225480102,"endMinutes":42,"userId":10,"scheduleId":10}]
         * device_type : kdszblock
         * yearSchedule : [{"scheduleStatus":"1","startTime":641225076,"updateTime":1588225322702,"endTime":641311476,"userId":0,"scheduleId":0},{"scheduleStatus":"1","startTime":641499211,"updateTime":1588225332007,"endTime":641585611,"userId":3,"scheduleId":3},{"scheduleStatus":"1","startTime":641499367,"updateTime":1588225335197,"endTime":641585767,"userId":4,"scheduleId":4}]
         * deviceId : Z102201410045
         * lockversion : 8100Z;00;V1.02.040;V0.00.000
         * pwdList : [{"userStatus":"1","updateTime":1588138739505,"userType":1,"pwd":"","userId":0},{"userStatus":"1","updateTime":1588138743218,"userType":0,"pwd":"","userId":2},{"userStatus":"1","updateTime":1588138740930,"userType":0,"pwd":"","userId":1}]
         * nwaddr : 9718
         * moduletype : KSZG1703U
         * eventTime : 2020-04-30 13:41:57.426
         * shortAddr : 10000
         * onlineTime : 2020-04-30 13:41:57.426
         * model : 8100Z
         * offlineTime : 2020-04-30 13:13:28.359
         * endpointList : [{"endpoint":10,"outputClusters":{"doorLockInfo":{"lockInfo":{"numberOfWeekDaySchedulesSupportedPerUser":220,"minRFIDCodeLength":4,"maxPINCodeLength":12,"minPINCodeLength":6,"numberOfPINUsersSupported":20,"numberOfRFIDUsersSupported":100,"updateTime":1588225572947,"numberOfYearDaySchedulesSupportedPerUser":220,"numberOfTotalUsersSupported":0,"maxRFIDCodeLength":10},"operationalSettings":{"soundVolume":2,"updateTime":1588225306881,"language":"zh"},"basicInfo":{"updateTime":1588225317368,"lockState":"lock"}},"powerInfo":{"batteryOneInfo":{"percentageRemaining":0,"updateTime":1588225299595,"voltage":0}},"basicInfo":{"manufacturerVersionDesc":"8100Z;00;V1.02.040;V0.00.000","hardwareVersion":16,"softwareInfo":"V1.04.004","model":"Z102201410045","updateTime":1588225296203}}}]
         * ipaddr :
         * SW : V1.04.004
         * moduleType :
         * delectTime : 2020-04-30 13:40:08.256
         * nickName : 姐姐
         * event_str : online
         * updateTime : 1588225296287
         * nodeType : 1
         * macaddr : 00:12:4b:00:21:61:e0:63
         * endpointCount : 10
         * time : 2020-04-30 10:24:29.659
         * _id : 5ea54e534d27d6da127dcd9c
         * zigbeeIEEE : 00:12:4b:00:21:61:e0:63
         */
        private String headersIp;
        private int shareFlag;
        private List<WeekScheduleEntity> weekSchedule;
        private String device_type;
        private List<YearScheduleEntity> yearSchedule;
        private String deviceId;
        private String lockversion;
        private List<PwdListEntity> pwdList;
        private int nwaddr;
        private String moduletype;
        private String eventTime;
        private int shortAddr;
        private String onlineTime;
        private String model;
        private String offlineTime;
        private List<EndpointListEntity> endpointList;
        private String ipaddr;
        private String SW;
        private String moduleType;
        private String delectTime;
        private String nickName;
        private String event_str;
        private long updateTime;
        private int nodeType;
        private String macaddr;
        private int endpointCount;
        private String time;
        private String _id;
        private String zigbeeIEEE;

        public void setHeadersIp(String headersIp) {
            this.headersIp = headersIp;
        }

        public void setShareFlag(int shareFlag) {
            this.shareFlag = shareFlag;
        }

        public void setWeekSchedule(List<WeekScheduleEntity> weekSchedule) {
            this.weekSchedule = weekSchedule;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public void setYearSchedule(List<YearScheduleEntity> yearSchedule) {
            this.yearSchedule = yearSchedule;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public void setLockversion(String lockversion) {
            this.lockversion = lockversion;
        }

        public void setPwdList(List<PwdListEntity> pwdList) {
            this.pwdList = pwdList;
        }

        public void setNwaddr(int nwaddr) {
            this.nwaddr = nwaddr;
        }

        public void setModuletype(String moduletype) {
            this.moduletype = moduletype;
        }

        public void setEventTime(String eventTime) {
            this.eventTime = eventTime;
        }

        public void setShortAddr(int shortAddr) {
            this.shortAddr = shortAddr;
        }

        public void setOnlineTime(String onlineTime) {
            this.onlineTime = onlineTime;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public void setOfflineTime(String offlineTime) {
            this.offlineTime = offlineTime;
        }

        public void setEndpointList(List<EndpointListEntity> endpointList) {
            this.endpointList = endpointList;
        }

        public void setIpaddr(String ipaddr) {
            this.ipaddr = ipaddr;
        }

        public void setSW(String SW) {
            this.SW = SW;
        }

        public void setModuleType(String moduleType) {
            this.moduleType = moduleType;
        }

        public void setDelectTime(String delectTime) {
            this.delectTime = delectTime;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public void setEvent_str(String event_str) {
            this.event_str = event_str;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public void setNodeType(int nodeType) {
            this.nodeType = nodeType;
        }

        public void setMacaddr(String macaddr) {
            this.macaddr = macaddr;
        }

        public void setEndpointCount(int endpointCount) {
            this.endpointCount = endpointCount;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public void setZigbeeIEEE(String zigbeeIEEE) {
            this.zigbeeIEEE = zigbeeIEEE;
        }

        public String getHeadersIp() {
            return headersIp;
        }

        public int getShareFlag() {
            return shareFlag;
        }

        public List<WeekScheduleEntity> getWeekSchedule() {
            return weekSchedule;
        }

        public String getDevice_type() {
            return device_type;
        }

        public List<YearScheduleEntity> getYearSchedule() {
            return yearSchedule;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getLockversion() {
            return lockversion;
        }

        public List<PwdListEntity> getPwdList() {
            return pwdList;
        }

        public int getNwaddr() {
            return nwaddr;
        }

        public String getModuletype() {
            return moduletype;
        }

        public String getEventTime() {
            return eventTime;
        }

        public int getShortAddr() {
            return shortAddr;
        }

        public String getOnlineTime() {
            return onlineTime;
        }

        public String getModel() {
            return model;
        }

        public String getOfflineTime() {
            return offlineTime;
        }

        public List<EndpointListEntity> getEndpointList() {
            return endpointList;
        }

        public String getIpaddr() {
            return ipaddr;
        }

        public String getSW() {
            return SW;
        }

        public String getModuleType() {
            return moduleType;
        }

        public String getDelectTime() {
            return delectTime;
        }

        public String getNickName() {
            return nickName;
        }

        public String getEvent_str() {
            return event_str;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public int getNodeType() {
            return nodeType;
        }

        public String getMacaddr() {
            return macaddr;
        }

        public int getEndpointCount() {
            return endpointCount;
        }

        public String getTime() {
            return time;
        }

        public String get_id() {
            return _id;
        }

        public String getZigbeeIEEE() {
            return zigbeeIEEE;
        }

        public class WeekScheduleEntity {
            /**
             * endHour : 14
             * daysMask : 127
             * startHour : 13
             * startMinutes : 42
             * scheduleStatus : 1
             * updateTime : 1588225480102
             * endMinutes : 42
             * userId : 10
             * scheduleId : 10
             */
            private int endHour;
            private int daysMask;
            private int startHour;
            private int startMinutes;
            private String scheduleStatus;
            private long updateTime;
            private int endMinutes;
            private int userId;
            private int scheduleId;

            public void setEndHour(int endHour) {
                this.endHour = endHour;
            }

            public void setDaysMask(int daysMask) {
                this.daysMask = daysMask;
            }

            public void setStartHour(int startHour) {
                this.startHour = startHour;
            }

            public void setStartMinutes(int startMinutes) {
                this.startMinutes = startMinutes;
            }

            public void setScheduleStatus(String scheduleStatus) {
                this.scheduleStatus = scheduleStatus;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }

            public void setEndMinutes(int endMinutes) {
                this.endMinutes = endMinutes;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public void setScheduleId(int scheduleId) {
                this.scheduleId = scheduleId;
            }

            public int getEndHour() {
                return endHour;
            }

            public int getDaysMask() {
                return daysMask;
            }

            public int getStartHour() {
                return startHour;
            }

            public int getStartMinutes() {
                return startMinutes;
            }

            public String getScheduleStatus() {
                return scheduleStatus;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public int getEndMinutes() {
                return endMinutes;
            }

            public int getUserId() {
                return userId;
            }

            public int getScheduleId() {
                return scheduleId;
            }
        }

        public class YearScheduleEntity {
            /**
             * scheduleStatus : 1
             * startTime : 641225076
             * updateTime : 1588225322702
             * endTime : 641311476
             * userId : 0
             * scheduleId : 0
             */
            private String scheduleStatus;
            private int startTime;
            private long updateTime;
            private int endTime;
            private int userId;
            private int scheduleId;

            public void setScheduleStatus(String scheduleStatus) {
                this.scheduleStatus = scheduleStatus;
            }

            public void setStartTime(int startTime) {
                this.startTime = startTime;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }

            public void setEndTime(int endTime) {
                this.endTime = endTime;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public void setScheduleId(int scheduleId) {
                this.scheduleId = scheduleId;
            }

            public String getScheduleStatus() {
                return scheduleStatus;
            }

            public int getStartTime() {
                return startTime;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public int getEndTime() {
                return endTime;
            }

            public int getUserId() {
                return userId;
            }

            public int getScheduleId() {
                return scheduleId;
            }
        }

        public class PwdListEntity {
            /**
             * userStatus : 1
             * updateTime : 1588138739505
             * userType : 1
             * pwd :
             * userId : 0
             */
            private String userStatus;
            private long updateTime;
            private int userType;
            private String pwd;
            private int userId;

            public void setUserStatus(String userStatus) {
                this.userStatus = userStatus;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }

            public void setUserType(int userType) {
                this.userType = userType;
            }

            public void setPwd(String pwd) {
                this.pwd = pwd;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getUserStatus() {
                return userStatus;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public int getUserType() {
                return userType;
            }

            public String getPwd() {
                return pwd;
            }

            public int getUserId() {
                return userId;
            }
        }

        public class EndpointListEntity {
            /**
             * endpoint : 10
             * outputClusters : {"doorLockInfo":{"lockInfo":{"numberOfWeekDaySchedulesSupportedPerUser":220,"minRFIDCodeLength":4,"maxPINCodeLength":12,"minPINCodeLength":6,"numberOfPINUsersSupported":20,"numberOfRFIDUsersSupported":100,"updateTime":1588225572947,"numberOfYearDaySchedulesSupportedPerUser":220,"numberOfTotalUsersSupported":0,"maxRFIDCodeLength":10},"operationalSettings":{"soundVolume":2,"updateTime":1588225306881,"language":"zh"},"basicInfo":{"updateTime":1588225317368,"lockState":"lock"}},"powerInfo":{"batteryOneInfo":{"percentageRemaining":0,"updateTime":1588225299595,"voltage":0}},"basicInfo":{"manufacturerVersionDesc":"8100Z;00;V1.02.040;V0.00.000","hardwareVersion":16,"softwareInfo":"V1.04.004","model":"Z102201410045","updateTime":1588225296203}}
             */
            private int endpoint;
            private OutputClustersEntity outputClusters;

            public void setEndpoint(int endpoint) {
                this.endpoint = endpoint;
            }

            public void setOutputClusters(OutputClustersEntity outputClusters) {
                this.outputClusters = outputClusters;
            }

            public int getEndpoint() {
                return endpoint;
            }

            public OutputClustersEntity getOutputClusters() {
                return outputClusters;
            }

            public class OutputClustersEntity {
                /**
                 * doorLockInfo : {"lockInfo":{"numberOfWeekDaySchedulesSupportedPerUser":220,"minRFIDCodeLength":4,"maxPINCodeLength":12,"minPINCodeLength":6,"numberOfPINUsersSupported":20,"numberOfRFIDUsersSupported":100,"updateTime":1588225572947,"numberOfYearDaySchedulesSupportedPerUser":220,"numberOfTotalUsersSupported":0,"maxRFIDCodeLength":10},"operationalSettings":{"soundVolume":2,"updateTime":1588225306881,"language":"zh"},"basicInfo":{"updateTime":1588225317368,"lockState":"lock"}}
                 * powerInfo : {"batteryOneInfo":{"percentageRemaining":0,"updateTime":1588225299595,"voltage":0}}
                 * basicInfo : {"manufacturerVersionDesc":"8100Z;00;V1.02.040;V0.00.000","hardwareVersion":16,"softwareInfo":"V1.04.004","model":"Z102201410045","updateTime":1588225296203}
                 */
                private DoorLockInfoEntity doorLockInfo;
                private PowerInfoEntity powerInfo;
                private BasicInfoEntity basicInfo;

                public void setDoorLockInfo(DoorLockInfoEntity doorLockInfo) {
                    this.doorLockInfo = doorLockInfo;
                }

                public void setPowerInfo(PowerInfoEntity powerInfo) {
                    this.powerInfo = powerInfo;
                }

                public void setBasicInfo(BasicInfoEntity basicInfo) {
                    this.basicInfo = basicInfo;
                }

                public DoorLockInfoEntity getDoorLockInfo() {
                    return doorLockInfo;
                }

                public PowerInfoEntity getPowerInfo() {
                    return powerInfo;
                }

                public BasicInfoEntity getBasicInfo() {
                    return basicInfo;
                }

                public class DoorLockInfoEntity {
                    /**
                     * lockInfo : {"numberOfWeekDaySchedulesSupportedPerUser":220,"minRFIDCodeLength":4,"maxPINCodeLength":12,"minPINCodeLength":6,"numberOfPINUsersSupported":20,"numberOfRFIDUsersSupported":100,"updateTime":1588225572947,"numberOfYearDaySchedulesSupportedPerUser":220,"numberOfTotalUsersSupported":0,"maxRFIDCodeLength":10}
                     * operationalSettings : {"soundVolume":2,"updateTime":1588225306881,"language":"zh"}
                     * basicInfo : {"updateTime":1588225317368,"lockState":"lock"}
                     */
                    private LockInfoEntity lockInfo;
                    private OperationalSettingsEntity operationalSettings;
                    private BasicInfoEntity basicInfo;

                    public void setLockInfo(LockInfoEntity lockInfo) {
                        this.lockInfo = lockInfo;
                    }

                    public void setOperationalSettings(OperationalSettingsEntity operationalSettings) {
                        this.operationalSettings = operationalSettings;
                    }

                    public void setBasicInfo(BasicInfoEntity basicInfo) {
                        this.basicInfo = basicInfo;
                    }

                    public LockInfoEntity getLockInfo() {
                        return lockInfo;
                    }

                    public OperationalSettingsEntity getOperationalSettings() {
                        return operationalSettings;
                    }

                    public BasicInfoEntity getBasicInfo() {
                        return basicInfo;
                    }

                    public class LockInfoEntity {
                        /**
                         * numberOfWeekDaySchedulesSupportedPerUser : 220
                         * minRFIDCodeLength : 4
                         * maxPINCodeLength : 12
                         * minPINCodeLength : 6
                         * numberOfPINUsersSupported : 20
                         * numberOfRFIDUsersSupported : 100
                         * updateTime : 1588225572947
                         * numberOfYearDaySchedulesSupportedPerUser : 220
                         * numberOfTotalUsersSupported : 0
                         * maxRFIDCodeLength : 10
                         */
                        private int numberOfWeekDaySchedulesSupportedPerUser;
                        private int minRFIDCodeLength;
                        private int maxPINCodeLength;
                        private int minPINCodeLength;
                        private int numberOfPINUsersSupported;
                        private int numberOfRFIDUsersSupported;
                        private long updateTime;
                        private int numberOfYearDaySchedulesSupportedPerUser;
                        private int numberOfTotalUsersSupported;
                        private int maxRFIDCodeLength;

                        public void setNumberOfWeekDaySchedulesSupportedPerUser(int numberOfWeekDaySchedulesSupportedPerUser) {
                            this.numberOfWeekDaySchedulesSupportedPerUser = numberOfWeekDaySchedulesSupportedPerUser;
                        }

                        public void setMinRFIDCodeLength(int minRFIDCodeLength) {
                            this.minRFIDCodeLength = minRFIDCodeLength;
                        }

                        public void setMaxPINCodeLength(int maxPINCodeLength) {
                            this.maxPINCodeLength = maxPINCodeLength;
                        }

                        public void setMinPINCodeLength(int minPINCodeLength) {
                            this.minPINCodeLength = minPINCodeLength;
                        }

                        public void setNumberOfPINUsersSupported(int numberOfPINUsersSupported) {
                            this.numberOfPINUsersSupported = numberOfPINUsersSupported;
                        }

                        public void setNumberOfRFIDUsersSupported(int numberOfRFIDUsersSupported) {
                            this.numberOfRFIDUsersSupported = numberOfRFIDUsersSupported;
                        }

                        public void setUpdateTime(long updateTime) {
                            this.updateTime = updateTime;
                        }

                        public void setNumberOfYearDaySchedulesSupportedPerUser(int numberOfYearDaySchedulesSupportedPerUser) {
                            this.numberOfYearDaySchedulesSupportedPerUser = numberOfYearDaySchedulesSupportedPerUser;
                        }

                        public void setNumberOfTotalUsersSupported(int numberOfTotalUsersSupported) {
                            this.numberOfTotalUsersSupported = numberOfTotalUsersSupported;
                        }

                        public void setMaxRFIDCodeLength(int maxRFIDCodeLength) {
                            this.maxRFIDCodeLength = maxRFIDCodeLength;
                        }

                        public int getNumberOfWeekDaySchedulesSupportedPerUser() {
                            return numberOfWeekDaySchedulesSupportedPerUser;
                        }

                        public int getMinRFIDCodeLength() {
                            return minRFIDCodeLength;
                        }

                        public int getMaxPINCodeLength() {
                            return maxPINCodeLength;
                        }

                        public int getMinPINCodeLength() {
                            return minPINCodeLength;
                        }

                        public int getNumberOfPINUsersSupported() {
                            return numberOfPINUsersSupported;
                        }

                        public int getNumberOfRFIDUsersSupported() {
                            return numberOfRFIDUsersSupported;
                        }

                        public long getUpdateTime() {
                            return updateTime;
                        }

                        public int getNumberOfYearDaySchedulesSupportedPerUser() {
                            return numberOfYearDaySchedulesSupportedPerUser;
                        }

                        public int getNumberOfTotalUsersSupported() {
                            return numberOfTotalUsersSupported;
                        }

                        public int getMaxRFIDCodeLength() {
                            return maxRFIDCodeLength;
                        }
                    }

                    public class OperationalSettingsEntity {
                        /**
                         * soundVolume : 2
                         * updateTime : 1588225306881
                         * language : zh
                         */
                        private int soundVolume;
                        private long updateTime;
                        private String language;

                        public void setSoundVolume(int soundVolume) {
                            this.soundVolume = soundVolume;
                        }

                        public void setUpdateTime(long updateTime) {
                            this.updateTime = updateTime;
                        }

                        public void setLanguage(String language) {
                            this.language = language;
                        }

                        public int getSoundVolume() {
                            return soundVolume;
                        }

                        public long getUpdateTime() {
                            return updateTime;
                        }

                        public String getLanguage() {
                            return language;
                        }
                    }

                    public class BasicInfoEntity {
                        /**
                         * updateTime : 1588225317368
                         * lockState : lock
                         */
                        private long updateTime;
                        private String lockState;

                        public void setUpdateTime(long updateTime) {
                            this.updateTime = updateTime;
                        }

                        public void setLockState(String lockState) {
                            this.lockState = lockState;
                        }

                        public long getUpdateTime() {
                            return updateTime;
                        }

                        public String getLockState() {
                            return lockState;
                        }
                    }
                }

                public class PowerInfoEntity {
                    /**
                     * batteryOneInfo : {"percentageRemaining":0,"updateTime":1588225299595,"voltage":0}
                     */
                    private BatteryOneInfoEntity batteryOneInfo;

                    public void setBatteryOneInfo(BatteryOneInfoEntity batteryOneInfo) {
                        this.batteryOneInfo = batteryOneInfo;
                    }

                    public BatteryOneInfoEntity getBatteryOneInfo() {
                        return batteryOneInfo;
                    }

                    public class BatteryOneInfoEntity {
                        /**
                         * percentageRemaining : 0
                         * updateTime : 1588225299595
                         * voltage : 0
                         */
                        private int percentageRemaining;
                        private long updateTime;
                        private int voltage;

                        public void setPercentageRemaining(int percentageRemaining) {
                            this.percentageRemaining = percentageRemaining;
                        }

                        public void setUpdateTime(long updateTime) {
                            this.updateTime = updateTime;
                        }

                        public void setVoltage(int voltage) {
                            this.voltage = voltage;
                        }

                        public int getPercentageRemaining() {
                            return percentageRemaining;
                        }

                        public long getUpdateTime() {
                            return updateTime;
                        }

                        public int getVoltage() {
                            return voltage;
                        }
                    }
                }

                public class BasicInfoEntity {
                    /**
                     * manufacturerVersionDesc : 8100Z;00;V1.02.040;V0.00.000
                     * hardwareVersion : 16
                     * softwareInfo : V1.04.004
                     * model : Z102201410045
                     * updateTime : 1588225296203
                     */
                    private String manufacturerVersionDesc;
                    private int hardwareVersion;
                    private String softwareInfo;
                    private String model;
                    private long updateTime;

                    public void setManufacturerVersionDesc(String manufacturerVersionDesc) {
                        this.manufacturerVersionDesc = manufacturerVersionDesc;
                    }

                    public void setHardwareVersion(int hardwareVersion) {
                        this.hardwareVersion = hardwareVersion;
                    }

                    public void setSoftwareInfo(String softwareInfo) {
                        this.softwareInfo = softwareInfo;
                    }

                    public void setModel(String model) {
                        this.model = model;
                    }

                    public void setUpdateTime(long updateTime) {
                        this.updateTime = updateTime;
                    }

                    public String getManufacturerVersionDesc() {
                        return manufacturerVersionDesc;
                    }

                    public int getHardwareVersion() {
                        return hardwareVersion;
                    }

                    public String getSoftwareInfo() {
                        return softwareInfo;
                    }

                    public String getModel() {
                        return model;
                    }

                    public long getUpdateTime() {
                        return updateTime;
                    }
                }
            }
        }
    }
}
