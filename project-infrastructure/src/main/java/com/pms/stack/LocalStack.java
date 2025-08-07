package com.pms.stack;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.rds.*;

public class LocalStack extends Stack {

    private final Vpc vpc;

    public LocalStack(final App scope,
                      final String id,
                      final StackProps props) {
        super(scope, id, props);
        this.vpc = createVpc();

        DatabaseInstance authServiceDb = createDatabaseInstance("AuthServiceDB", "auth-service-db");
        DatabaseInstance patientServiceDb = createDatabaseInstance("PatientServiceDB", "patient-service-db");
    }

    private Vpc createVpc() {
        return Vpc.Builder
                // add the VPC to "this" stack
                .create(this, "PatientManagementSystemVPC")
                .vpcName("PatientManagementSystemVPC")
                .maxAzs(2)
                .build();
    }

    private DatabaseInstance createDatabaseInstance(String id, String dbName) {
        return DatabaseInstance.Builder
                .create(this, id)
                .engine(DatabaseInstanceEngine.postgres(
                        PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_17_2)
                                .build()))
                // add database to the VPC
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("admin_user"))
                .databaseName(dbName)
                // every time you destroy the stack, destroy the database (good for development only)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    public static void main(final String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());

        StackProps props = StackProps
                .builder()
                // used to convert our code into Cloud Formation Template
                .synthesizer(new BootstraplessSynthesizer())
                .build();

        new LocalStack(app, "localstack", props);
        app.synth();
    }
}
