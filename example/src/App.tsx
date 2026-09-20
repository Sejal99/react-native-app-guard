import { useState } from 'react';
import { Text, View, StyleSheet, Button } from 'react-native';
import AppGuard from 'react-native-app-guard';

export default function App() {
  const [rootStatus, setRootStatus] = useState<string>('Not checked');
  const [recordingStatus, setRecordingStatus] = useState<string>('Not checked');
  const [screenshotBlocked, setScreenshotBlockedState] =
    useState<boolean>(false);
  const [emulatorStatus, setEmulatorStatus] = useState<string>('Not checked');

  const checkRoot = () => {
    const result = AppGuard.isDeviceRooted();
    setRootStatus(result ? 'ROOTED' : 'Not rooted');
  };

  const checkRecording = () => {
    const result = AppGuard.isScreenRecording();
    setRecordingStatus(result ? 'Recording detected' : 'Not recording');
  };

  const toggleScreenshotBlock = () => {
    const newValue = !screenshotBlocked;
    AppGuard.setScreenshotBlocked(newValue);
    setScreenshotBlockedState(newValue);
  };

  const checkEmulator = () => {
    const result = AppGuard.isEmulator();
    setEmulatorStatus(result ? 'EMULATOR' : 'Real device');
  };

  return (
    <View style={styles.container}>
      <Text style={styles.label}>Root status: {rootStatus}</Text>
      <Button title="Check Root" onPress={checkRoot} />

      <View style={styles.spacer} />

      <Text style={styles.label}>Recording status: {recordingStatus}</Text>
      <Button title="Check Recording" onPress={checkRecording} />

      <View style={styles.spacer} />

      <Text style={styles.label}>
        Screenshot blocked: {screenshotBlocked ? 'YES' : 'NO'}
      </Text>
      <Button title="Toggle Screenshot Block" onPress={toggleScreenshotBlock} />

      <View style={styles.spacer} />

      <Text style={styles.label}>Emulator status: {emulatorStatus}</Text>
      <Button title="Check Emulator" onPress={checkEmulator} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 20,
  },
  label: {
    marginBottom: 10,
    fontSize: 16,
  },
  spacer: {
    height: 30,
  },
});
