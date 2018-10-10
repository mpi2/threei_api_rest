import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatToolbarModule, MatTooltipModule,MatMenuModule, MatButtonModule} from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';

import { MenuComponent } from './menu/menu.component';

@NgModule({
  imports: [
    MatToolbarModule, MatTooltipModule, CommonModule, MatMenuModule, BrowserAnimationsModule, MatButtonModule, RouterModule
  ],
  declarations: [MenuComponent],
  exports : [MenuComponent]
})
export class MenusModule { }
