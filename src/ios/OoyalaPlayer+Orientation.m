//
//  OoyalaPlayer+Orientation.m
//  Ooyala
//
//  Created by DongKai.Li on 1/6/15.
//
//

#import "OoyalaPlayer+Orientation.h"

@implementation UIViewController (Orientation)

- (NSUInteger)supportedInterfaceOrientations
{
    if ([self respondsToSelector:@selector(initWithPcode:domain:)]) {
        return UIInterfaceOrientationMaskLandscape;
    }
    
    return UIInterfaceOrientationMaskPortrait;
}

@end
