import { Component, OnInit } from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'threei-consent',
  templateUrl: './consent.component.html',
  styleUrls: ['./consent.component.css']
})
export class ConsentComponent {

  constructor(public snackBar: MatSnackBar) {
      this.snackBar.openFromComponent(CustomSnackBar, {
         duration: 20000000,
         verticalPosition: 'top',
         horizontalPosition: 'right',
      });
  }
}


// 'This site uses cookies: <a href="">Find out more.</a>', 'OK',

@Component({
  selector: 'custom-snackbar',
  template: '<span style="color: white">This site uses cookies: ' +
  '</span><a href="https://www.ebi.ac.uk/data-protection/privacy-notice/mousephenotype-org">'
  + 'Find out more.</a>&nbsp;&nbsp; <button mat-raised-button color="accent"  style="color: white" (click)="close()">OK</button>'
})
export class CustomSnackBar {
constructor(public snackBar: MatSnackBar) {}

  close() {
    this.snackBar.dismiss();
  }
 }
