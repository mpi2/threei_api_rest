import {DragDropModule} from '@angular/cdk/drag-drop';
import {ScrollingModule} from '@angular/cdk/scrolling';
import {CdkTableModule} from '@angular/cdk/table';
import {CdkTreeModule} from '@angular/cdk/tree';
import {NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {SlideshowModule} from 'ng-simple-slideshow';
import { PrivacyPageComponent } from './privacy-page/privacy-page.component';
import { LearnMorePageComponent } from './learn-more-page/learn-more-page.component';
import { HighchartsChartComponent } from './highcharts-chart.component';
import { FormsModule} from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule} from '@angular/forms';
import { MatAutocompleteModule,
  MatBadgeModule,
  MatBottomSheetModule,
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatCheckboxModule,
  MatChipsModule,
  MatDatepickerModule,
  MatDialogModule,
  MatDividerModule,
  MatExpansionModule,
  MatGridListModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatMenuModule,
  MatNativeDateModule,
  MatPaginatorModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
  MatRadioModule,
  MatRippleModule,
  MatSelectModule,
  MatSidenavModule,
  MatSliderModule,
  MatSlideToggleModule,
  MatSnackBarModule,
  MatSortModule,
  MatStepperModule,
  MatTableModule,
  MatTabsModule,
  MatToolbarModule,
  MatTooltipModule,
  MatTreeModule,
  MatIcon} from '@angular/material';
import { FlexLayoutModule } from '@angular/flex-layout';



import { AppComponent  } from './app.component';
import { MenusModule } from './menus/menus.module';
import { Router } from '@angular/router';
import { AppRoutingModule } from './app-routing.module';
import { HomePageComponent } from './home-page/home-page.component';
import { FaqPageComponent } from './faq-page/faq-page.component';
import { AnalysisPageComponent, SafePipe } from './analysis-page/analysis-page.component';
import { MethodsPageComponent } from './methods-page/methods-page.component';
import { ContactPageComponent } from './contact-page/contact-page.component';
import { GlossaryPageComponent } from './glossary-page/glossary-page.component';
import { PublicationsPageComponent } from './publications-page/publications-page.component';
import { ProjectPageComponent } from './project-page/project-page.component';
import { PhenotypeofthemonthPageComponent } from './phenotypeofthemonth-page/phenotypeofthemonth-page.component';
import { ImageacknowledgementsPageComponent } from './imageacknowledgements-page/imageacknowledgements-page.component';
import { CellHeatmapComponent } from './cell-heatmap/cell-heatmap.component';
import { ProcedureHeatmapComponent } from './procedure-heatmap/procedure-heatmap.component';
import { DetailsPageComponent } from './details-page/details-page.component';
import { PhenotypeOfTheMonthComponent } from './phenotype-of-the-month/phenotype-of-the-month.component';
import { SlideshowComponent } from './slideshow/slideshow.component';
import { StatsBlocksComponent } from './stats-blocks/stats-blocks.component';
import { LogosComponent } from './logos/logos.component';
import { TemplateComponent } from './template/template.component';
import { StrengthNLimitationsComponent } from './strength-n-limitations/strength-n-limitations.component';
import { AboutComponent } from './about/about.component';
import { ConsortiumComponent } from './consortium/consortium.component';
import { StatisticalDesignComponent } from './statistical-design/statistical-design.component';
import { FlowFilesComponent } from './flow-files/flow-files.component';
import { DataDownloadsComponent } from './data-downloads/data-downloads.component';
import { MapOfImmuneComponent } from './map-of-immune/map-of-immune.component';





@NgModule({
  declarations: [
    AppComponent,
    SafePipe,
    HomePageComponent,
    FaqPageComponent, AnalysisPageComponent, MethodsPageComponent, ContactPageComponent, GlossaryPageComponent,
    PublicationsPageComponent, PrivacyPageComponent, LearnMorePageComponent,
    HighchartsChartComponent,
    ProjectPageComponent,
    PhenotypeofthemonthPageComponent,
    ImageacknowledgementsPageComponent,
    CellHeatmapComponent,
    ProcedureHeatmapComponent, ProcedureHeatmapComponent, DetailsPageComponent, PhenotypeOfTheMonthComponent,
    SlideshowComponent, StatsBlocksComponent, LogosComponent, TemplateComponent, StrengthNLimitationsComponent,
    AboutComponent, ConsortiumComponent, StatisticalDesignComponent, FlowFilesComponent, DataDownloadsComponent, MapOfImmuneComponent
  ],
  imports: [
    MatSidenavModule, BrowserModule, MenusModule, AppRoutingModule, SlideshowModule, MatExpansionModule, FormsModule,
    HttpClientModule, MatRadioModule, MatProgressBarModule, MatSelectModule, MatInputModule, MatCardModule,
     MatTabsModule,  FlexLayoutModule, MatButtonModule, MatTabsModule, MatIconModule, ReactiveFormsModule, MatAutocompleteModule
  ],
  exports: [
    CdkTableModule,
    CdkTreeModule,
    DragDropModule,
    MatAutocompleteModule,
    MatBadgeModule,
    MatBottomSheetModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatCheckboxModule,
    MatChipsModule,
    MatStepperModule,
    MatDatepickerModule,
    MatDialogModule,
    MatDividerModule,
    MatExpansionModule,
    MatGridListModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatMenuModule,
    MatNativeDateModule,
    MatPaginatorModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatRippleModule,
    MatSelectModule,
    MatSidenavModule,
    MatSliderModule,
    MatSlideToggleModule,
    MatSnackBarModule,
    MatSortModule,
    MatTableModule,
    MatTabsModule,
    MatToolbarModule,
    MatTooltipModule,
    MatTreeModule,
    ScrollingModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
  // Diagnostic only: inspect router configuration
  constructor(router: Router) {
    // console.log('Routes: ', JSON.stringify(router.config, undefined, 2));
  }

 }
