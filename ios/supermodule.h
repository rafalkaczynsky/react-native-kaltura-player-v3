//  Created by react-native-create-bridge

// import UIKit so you can subclass off UIView
#import <UIKit/UIKit.h>

@class RCTEventDispatcher;

@interface supermodule : UIView

  // Define view properties here with @property
  @property (nonatomic, strong) NSArray *exampleProp;
  @property (retain, nonatomic) UIViewController *player; 

  // Initializing with the event dispatcher allows us to communicate with JS
  - (instancetype)initWithEventDispatcher:(RCTEventDispatcher *)eventDispatcher NS_DESIGNATED_INITIALIZER;

@end
