#import "AppGuard.h"
#import <UIKit/UIKit.h>

@implementation AppGuard

- (NSNumber *)isDeviceRooted {
  return @([self checkJailbreakPaths] || [self checkSandboxViolation]);
}

- (void)setScreenshotBlocked:(BOOL)blocked {
    dispatch_async(dispatch_get_main_queue(), ^{
        UIWindow *window = [UIApplication sharedApplication].windows.firstObject;
        if (!window) return;

        static UIView *secureOverlay = nil;

        if (blocked) {
            if (!secureOverlay) {
                secureOverlay = [[UIView alloc] initWithFrame:window.bounds];
                secureOverlay.backgroundColor = [UIColor blackColor];
                secureOverlay.tag = 999888;
            }

            // Listen for capture state changes (recording start/stop)
            [[NSNotificationCenter defaultCenter] addObserverForName:UIScreenCapturedDidChangeNotification
                                                               object:nil
                                                                queue:[NSOperationQueue mainQueue]
                                                           usingBlock:^(NSNotification * _Nonnull note) {
                if ([UIScreen mainScreen].isCaptured) {
                    if (![window.subviews containsObject:secureOverlay]) {
                        secureOverlay.frame = window.bounds;
                        [window addSubview:secureOverlay];
                    }
                } else {
                    [secureOverlay removeFromSuperview];
                }
            }];
        } else {
            [[NSNotificationCenter defaultCenter] removeObserver:self name:UIScreenCapturedDidChangeNotification object:nil];
            if (secureOverlay) {
                [secureOverlay removeFromSuperview];
            }
        }
    });
}

// Common jailbreak-related file paths
- (BOOL)checkJailbreakPaths {
  NSArray<NSString *> *paths = @[
    @"/Applications/Cydia.app",
    @"/Applications/Sileo.app",
    @"/Library/MobileSubstrate/MobileSubstrate.dylib",
    @"/usr/libexec/ssh-keysh",
    @"/bin/bash",
    @"/usr/sbin/sshd",
    @"/etc/apt",
    @"/private/var/lib/apt/"
  ];

  for (NSString *path in paths) {
    if ([[NSFileManager defaultManager] fileExistsAtPath:path]) {
      return YES;
    }
  }
  return NO;
}

- (NSNumber *)isEmulator {
#if TARGET_OS_SIMULATOR
    return @YES;
#else
    return @NO;
#endif
}

// Try writing outside the app's sandbox — only possible on jailbroken devices
- (BOOL)checkSandboxViolation {
  NSString *testPath = @"/private/jailbreak_test.txt";
  NSString *testString = @"test";
  NSError *error = nil;

  [testString writeToFile:testPath
               atomically:YES
                 encoding:NSUTF8StringEncoding
                    error:&error];

  if (error == nil) {
    [[NSFileManager defaultManager] removeItemAtPath:testPath error:nil];
    return YES;
  }
  return NO;
}

- (UIWindow *)topWindow {
  if (@available(iOS 13.0, *)) {
    for (UIScene *scene in UIApplication.sharedApplication.connectedScenes) {
      if ([scene isKindOfClass:UIWindowScene.class]) {
        UIWindowScene *windowScene = (UIWindowScene *)scene;
        if (windowScene.activationState ==
            UISceneActivationStateForegroundActive) {
          for (UIWindow *window in windowScene.windows) {
            if (window.isKeyWindow) {
              return window;
            }
          }
        }
      }
    }
    return nil;
  } else {
    return UIApplication.sharedApplication.keyWindow;
  }
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params {
  return std::make_shared<facebook::react::NativeAppGuardSpecJSI>(params);
}

+ (NSString *)moduleName {
  return @"AppGuard";
}

@end