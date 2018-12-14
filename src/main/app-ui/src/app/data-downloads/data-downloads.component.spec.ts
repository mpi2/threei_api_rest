import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataDownloadsComponent } from './data-downloads.component';

describe('DataDownloadsComponent', () => {
  let component: DataDownloadsComponent;
  let fixture: ComponentFixture<DataDownloadsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataDownloadsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataDownloadsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
