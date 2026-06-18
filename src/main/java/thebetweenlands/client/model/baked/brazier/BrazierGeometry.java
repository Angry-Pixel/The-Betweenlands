package thebetweenlands.client.model.baked.brazier;

import thebetweenlands.util.QuadBuilder;

/**
 * Procedurally generated geometry from the 1.12.2 Tabula
 */
public final class BrazierGeometry {

	private BrazierGeometry() {
	}

	public static void build(QuadBuilder builder) {
		//brazier_base
		builder.addVertex(-0F, 2F, 0F, 2F, 2F);
		builder.addVertex(1F, 2F, -0F, 4F, 2F);
		builder.addVertex(1F, 1.8125F, -0F, 4F, 2.375F);
		builder.addVertex(-0F, 1.8125F, 0F, 2F, 2.375F);

		builder.addVertex(0F, 2F, 1F, 8F, 2F);
		builder.addVertex(0F, 1.8125F, 1F, 8F, 2.375F);
		builder.addVertex(1F, 1.8125F, 1F, 6F, 2.375F);
		builder.addVertex(1F, 2F, 1F, 6F, 2F);

		builder.addVertex(-0F, 1.8125F, 0F, 4F, 2F);
		builder.addVertex(1F, 1.8125F, -0F, 6F, 2F);
		builder.addVertex(1F, 1.8125F, 1F, 6F, 0F);
		builder.addVertex(0F, 1.8125F, 1F, 4F, 0F);

		builder.addVertex(-0F, 2F, 0F, 2F, 2F);
		builder.addVertex(0F, 2F, 1F, 2F, 0F);
		builder.addVertex(1F, 2F, 1F, 4F, 0F);
		builder.addVertex(1F, 2F, -0F, 4F, 2F);

		builder.addVertex(1F, 2F, -0F, 4F, 2F);
		builder.addVertex(1F, 2F, 1F, 6F, 2F);
		builder.addVertex(1F, 1.8125F, 1F, 6F, 2.375F);
		builder.addVertex(1F, 1.8125F, -0F, 4F, 2.375F);

		builder.addVertex(-0F, 2F, 0F, 2F, 2F);
		builder.addVertex(-0F, 1.8125F, 0F, 2F, 2.375F);
		builder.addVertex(0F, 1.8125F, 1F, 0F, 2.375F);
		builder.addVertex(0F, 2F, 1F, 0F, 2F);

		//standard_back_right2_1
		builder.addVertex(0.128147F, 0.602066F, 0.685019F, 11.25F, 13.125F);
		builder.addVertex(0.312547F, 0.568206F, 0.687549F, 11.625F, 13.125F);
		builder.addVertex(0.188775F, -0.096567F, 0.811704F, 11.625F, 14.5F);
		builder.addVertex(0.004375F, -0.062706F, 0.809175F, 11.25F, 14.5F);

		builder.addVertex(0.131816F, 0.635822F, 0.869419F, 12.375F, 13.125F);
		builder.addVertex(0.008044F, -0.02895F, 0.993574F, 12.375F, 14.5F);
		builder.addVertex(0.192444F, -0.06281F, 0.996104F, 12F, 14.5F);
		builder.addVertex(0.316216F, 0.601962F, 0.871949F, 12F, 13.125F);

		builder.addVertex(0.004375F, -0.062706F, 0.809175F, 11.625F, 13.125F);
		builder.addVertex(0.188775F, -0.096567F, 0.811704F, 12F, 13.125F);
		builder.addVertex(0.192444F, -0.06281F, 0.996104F, 12F, 12.75F);
		builder.addVertex(0.008044F, -0.02895F, 0.993574F, 11.625F, 12.75F);

		builder.addVertex(0.128147F, 0.602066F, 0.685019F, 11.25F, 13.125F);
		builder.addVertex(0.131816F, 0.635822F, 0.869419F, 11.25F, 12.75F);
		builder.addVertex(0.316216F, 0.601962F, 0.871949F, 11.625F, 12.75F);
		builder.addVertex(0.312547F, 0.568206F, 0.687549F, 11.625F, 13.125F);

		builder.addVertex(0.312547F, 0.568206F, 0.687549F, 11.625F, 13.125F);
		builder.addVertex(0.316216F, 0.601962F, 0.871949F, 12F, 13.125F);
		builder.addVertex(0.192444F, -0.06281F, 0.996104F, 12F, 14.5F);
		builder.addVertex(0.188775F, -0.096567F, 0.811704F, 11.625F, 14.5F);

		builder.addVertex(0.128147F, 0.602066F, 0.685019F, 11.25F, 13.125F);
		builder.addVertex(0.004375F, -0.062706F, 0.809175F, 11.25F, 14.5F);
		builder.addVertex(0.008044F, -0.02895F, 0.993574F, 10.875F, 14.5F);
		builder.addVertex(0.131816F, 0.635822F, 0.869419F, 10.875F, 13.125F);

		//standardrope_back_right2
		builder.addVertex(0.105165F, 0.663459F, 0.645488F, 6.5F, 15.125F);
		builder.addVertex(0.352826F, 0.629366F, 0.646802F, 7F, 15.125F);
		builder.addVertex(0.335849F, 0.506698F, 0.663807F, 7F, 15.375F);
		builder.addVertex(0.088188F, 0.54079F, 0.662494F, 6.5F, 15.375F);

		builder.addVertex(0.108514F, 0.69733F, 0.893161F, 8F, 15.125F);
		builder.addVertex(0.091538F, 0.574662F, 0.910166F, 8F, 15.375F);
		builder.addVertex(0.339199F, 0.540569F, 0.91148F, 7.5F, 15.375F);
		builder.addVertex(0.356175F, 0.663238F, 0.894474F, 7.5F, 15.125F);

		builder.addVertex(0.088188F, 0.54079F, 0.662494F, 7F, 15.125F);
		builder.addVertex(0.335849F, 0.506698F, 0.663807F, 7.5F, 15.125F);
		builder.addVertex(0.339199F, 0.540569F, 0.91148F, 7.5F, 14.625F);
		builder.addVertex(0.091538F, 0.574662F, 0.910166F, 7F, 14.625F);

		builder.addVertex(0.105165F, 0.663459F, 0.645488F, 6.5F, 15.125F);
		builder.addVertex(0.108514F, 0.69733F, 0.893161F, 6.5F, 14.625F);
		builder.addVertex(0.356175F, 0.663238F, 0.894474F, 7F, 14.625F);
		builder.addVertex(0.352826F, 0.629366F, 0.646802F, 7F, 15.125F);

		builder.addVertex(0.352826F, 0.629366F, 0.646802F, 7F, 15.125F);
		builder.addVertex(0.356175F, 0.663238F, 0.894474F, 7.5F, 15.125F);
		builder.addVertex(0.339199F, 0.540569F, 0.91148F, 7.5F, 15.375F);
		builder.addVertex(0.335849F, 0.506698F, 0.663807F, 7F, 15.375F);

		builder.addVertex(0.105165F, 0.663459F, 0.645488F, 6.5F, 15.125F);
		builder.addVertex(0.088188F, 0.54079F, 0.662494F, 6.5F, 15.375F);
		builder.addVertex(0.091538F, 0.574662F, 0.910166F, 6F, 15.375F);
		builder.addVertex(0.108514F, 0.69733F, 0.893161F, 6F, 15.125F);

		//standard_base
		builder.addVertex(0.125F, 1.53125F, 0.125F, 7.5F, 8.75F);
		builder.addVertex(0.875F, 1.53125F, 0.125F, 9F, 8.75F);
		builder.addVertex(0.875F, 1.34375F, 0.125F, 9F, 9.125F);
		builder.addVertex(0.125F, 1.34375F, 0.125F, 7.5F, 9.125F);

		builder.addVertex(0.125F, 1.53125F, 0.875F, 12F, 8.75F);
		builder.addVertex(0.125F, 1.34375F, 0.875F, 12F, 9.125F);
		builder.addVertex(0.875F, 1.34375F, 0.875F, 10.5F, 9.125F);
		builder.addVertex(0.875F, 1.53125F, 0.875F, 10.5F, 8.75F);

		builder.addVertex(0.125F, 1.34375F, 0.125F, 9F, 8.75F);
		builder.addVertex(0.875F, 1.34375F, 0.125F, 10.5F, 8.75F);
		builder.addVertex(0.875F, 1.34375F, 0.875F, 10.5F, 7.25F);
		builder.addVertex(0.125F, 1.34375F, 0.875F, 9F, 7.25F);

		builder.addVertex(0.125F, 1.53125F, 0.125F, 7.5F, 8.75F);
		builder.addVertex(0.125F, 1.53125F, 0.875F, 7.5F, 7.25F);
		builder.addVertex(0.875F, 1.53125F, 0.875F, 9F, 7.25F);
		builder.addVertex(0.875F, 1.53125F, 0.125F, 9F, 8.75F);

		builder.addVertex(0.875F, 1.53125F, 0.125F, 9F, 8.75F);
		builder.addVertex(0.875F, 1.53125F, 0.875F, 10.5F, 8.75F);
		builder.addVertex(0.875F, 1.34375F, 0.875F, 10.5F, 9.125F);
		builder.addVertex(0.875F, 1.34375F, 0.125F, 9F, 9.125F);

		builder.addVertex(0.125F, 1.53125F, 0.125F, 7.5F, 8.75F);
		builder.addVertex(0.125F, 1.34375F, 0.125F, 7.5F, 9.125F);
		builder.addVertex(0.125F, 1.34375F, 0.875F, 6F, 9.125F);
		builder.addVertex(0.125F, 1.53125F, 0.875F, 6F, 8.75F);

		//brazier_edge_back1
		builder.addVertex(0F, 1.996902F, 0.966056F, 0.375F, 9.625F);
		builder.addVertex(1F, 1.996902F, 0.966056F, 2.375F, 9.625F);
		builder.addVertex(1F, 1.8125F, 1F, 2.375F, 10F);
		builder.addVertex(0F, 1.8125F, 1F, 0.375F, 10F);

		builder.addVertex(0F, 2.030846F, 1.150458F, 4.75F, 9.625F);
		builder.addVertex(0F, 1.846444F, 1.184402F, 4.75F, 10F);
		builder.addVertex(1F, 1.846444F, 1.184402F, 2.75F, 10F);
		builder.addVertex(1F, 2.030846F, 1.150458F, 2.75F, 9.625F);

		builder.addVertex(0F, 1.8125F, 1F, 2.375F, 9.625F);
		builder.addVertex(1F, 1.8125F, 1F, 4.375F, 9.625F);
		builder.addVertex(1F, 1.846444F, 1.184402F, 4.375F, 9.25F);
		builder.addVertex(0F, 1.846444F, 1.184402F, 2.375F, 9.25F);

		builder.addVertex(0F, 1.996902F, 0.966056F, 0.375F, 9.625F);
		builder.addVertex(0F, 2.030846F, 1.150458F, 0.375F, 9.25F);
		builder.addVertex(1F, 2.030846F, 1.150458F, 2.375F, 9.25F);
		builder.addVertex(1F, 1.996902F, 0.966056F, 2.375F, 9.625F);

		builder.addVertex(1F, 1.996902F, 0.966056F, 2.375F, 9.625F);
		builder.addVertex(1F, 2.030846F, 1.150458F, 2.75F, 9.625F);
		builder.addVertex(1F, 1.846444F, 1.184402F, 2.75F, 10F);
		builder.addVertex(1F, 1.8125F, 1F, 2.375F, 10F);

		builder.addVertex(0F, 1.996902F, 0.966056F, 0.375F, 9.625F);
		builder.addVertex(0F, 1.8125F, 1F, 0.375F, 10F);
		builder.addVertex(0F, 1.846444F, 1.184402F, 0F, 10F);
		builder.addVertex(0F, 2.030846F, 1.150458F, 0F, 9.625F);

		//support_front_left1a
		builder.addVertex(1F, 2.125F, -0.1875F, 8.5F, 0.375F);
		builder.addVertex(1.1875F, 2.125F, -0.1875F, 8.875F, 0.375F);
		builder.addVertex(1.1875F, 1.8125F, -0.1875F, 8.875F, 1F);
		builder.addVertex(1F, 1.8125F, -0.1875F, 8.5F, 1F);

		builder.addVertex(1F, 2.125F, -0F, 9.625F, 0.375F);
		builder.addVertex(1F, 1.8125F, -0F, 9.625F, 1F);
		builder.addVertex(1.1875F, 1.8125F, -0F, 9.25F, 1F);
		builder.addVertex(1.1875F, 2.125F, -0F, 9.25F, 0.375F);

		builder.addVertex(1F, 1.8125F, -0.1875F, 8.875F, 0.375F);
		builder.addVertex(1.1875F, 1.8125F, -0.1875F, 9.25F, 0.375F);
		builder.addVertex(1.1875F, 1.8125F, -0F, 9.25F, 0F);
		builder.addVertex(1F, 1.8125F, -0F, 8.875F, 0F);

		builder.addVertex(1F, 2.125F, -0.1875F, 8.5F, 0.375F);
		builder.addVertex(1F, 2.125F, -0F, 8.5F, 0F);
		builder.addVertex(1.1875F, 2.125F, -0F, 8.875F, 0F);
		builder.addVertex(1.1875F, 2.125F, -0.1875F, 8.875F, 0.375F);

		builder.addVertex(1.1875F, 2.125F, -0.1875F, 8.875F, 0.375F);
		builder.addVertex(1.1875F, 2.125F, -0F, 9.25F, 0.375F);
		builder.addVertex(1.1875F, 1.8125F, -0F, 9.25F, 1F);
		builder.addVertex(1.1875F, 1.8125F, -0.1875F, 8.875F, 1F);

		builder.addVertex(1F, 2.125F, -0.1875F, 8.5F, 0.375F);
		builder.addVertex(1F, 1.8125F, -0.1875F, 8.5F, 1F);
		builder.addVertex(1F, 1.8125F, -0F, 8.125F, 1F);
		builder.addVertex(1F, 2.125F, -0F, 8.125F, 0.375F);

		//standard_back_left3
		builder.addVertex(0.687452F, 0.568206F, 0.687549F, 9.625F, 13.125F);
		builder.addVertex(0.871852F, 0.602066F, 0.685019F, 10F, 13.125F);
		builder.addVertex(0.995624F, -0.062706F, 0.809174F, 10F, 14.5F);
		builder.addVertex(0.811224F, -0.096567F, 0.811704F, 9.625F, 14.5F);

		builder.addVertex(0.683784F, 0.601962F, 0.871949F, 10.75F, 13.125F);
		builder.addVertex(0.807556F, -0.062811F, 0.996104F, 10.75F, 14.5F);
		builder.addVertex(0.991956F, -0.02895F, 0.993574F, 10.375F, 14.5F);
		builder.addVertex(0.868183F, 0.635822F, 0.869419F, 10.375F, 13.125F);

		builder.addVertex(0.811224F, -0.096567F, 0.811704F, 10F, 13.125F);
		builder.addVertex(0.995624F, -0.062706F, 0.809174F, 10.375F, 13.125F);
		builder.addVertex(0.991956F, -0.02895F, 0.993574F, 10.375F, 12.75F);
		builder.addVertex(0.807556F, -0.062811F, 0.996104F, 10F, 12.75F);

		builder.addVertex(0.687452F, 0.568206F, 0.687549F, 9.625F, 13.125F);
		builder.addVertex(0.683784F, 0.601962F, 0.871949F, 9.625F, 12.75F);
		builder.addVertex(0.868183F, 0.635822F, 0.869419F, 10F, 12.75F);
		builder.addVertex(0.871852F, 0.602066F, 0.685019F, 10F, 13.125F);

		builder.addVertex(0.871852F, 0.602066F, 0.685019F, 10F, 13.125F);
		builder.addVertex(0.868183F, 0.635822F, 0.869419F, 10.375F, 13.125F);
		builder.addVertex(0.991956F, -0.02895F, 0.993574F, 10.375F, 14.5F);
		builder.addVertex(0.995624F, -0.062706F, 0.809174F, 10F, 14.5F);

		builder.addVertex(0.687452F, 0.568206F, 0.687549F, 9.625F, 13.125F);
		builder.addVertex(0.811224F, -0.096567F, 0.811704F, 9.625F, 14.5F);
		builder.addVertex(0.807556F, -0.062811F, 0.996104F, 9.25F, 14.5F);
		builder.addVertex(0.683784F, 0.601962F, 0.871949F, 9.25F, 13.125F);

		//support_front_left1e
		builder.addVertex(0.99875F, 1.661816F, -0.075918F, 8.5F, 4.875F);
		builder.addVertex(1.18625F, 1.661816F, -0.075918F, 8.875F, 4.875F);
		builder.addVertex(1.18625F, 1.497732F, 0.462118F, 8.875F, 6F);
		builder.addVertex(0.99875F, 1.497732F, 0.462118F, 8.5F, 6F);

		builder.addVertex(0.99875F, 1.841161F, -0.021224F, 9.625F, 4.875F);
		builder.addVertex(0.99875F, 1.677078F, 0.516812F, 9.625F, 6F);
		builder.addVertex(1.18625F, 1.677078F, 0.516812F, 9.25F, 6F);
		builder.addVertex(1.18625F, 1.841161F, -0.021224F, 9.25F, 4.875F);

		builder.addVertex(0.99875F, 1.497732F, 0.462118F, 8.875F, 4.875F);
		builder.addVertex(1.18625F, 1.497732F, 0.462118F, 9.25F, 4.875F);
		builder.addVertex(1.18625F, 1.677078F, 0.516812F, 9.25F, 4.5F);
		builder.addVertex(0.99875F, 1.677078F, 0.516812F, 8.875F, 4.5F);

		builder.addVertex(0.99875F, 1.661816F, -0.075918F, 8.5F, 4.875F);
		builder.addVertex(0.99875F, 1.841161F, -0.021224F, 8.5F, 4.5F);
		builder.addVertex(1.18625F, 1.841161F, -0.021224F, 8.875F, 4.5F);
		builder.addVertex(1.18625F, 1.661816F, -0.075918F, 8.875F, 4.875F);

		builder.addVertex(1.18625F, 1.661816F, -0.075918F, 8.875F, 4.875F);
		builder.addVertex(1.18625F, 1.841161F, -0.021224F, 9.25F, 4.875F);
		builder.addVertex(1.18625F, 1.677078F, 0.516812F, 9.25F, 6F);
		builder.addVertex(1.18625F, 1.497732F, 0.462118F, 8.875F, 6F);

		builder.addVertex(0.99875F, 1.661816F, -0.075918F, 8.5F, 4.875F);
		builder.addVertex(0.99875F, 1.497732F, 0.462118F, 8.5F, 6F);
		builder.addVertex(0.99875F, 1.677078F, 0.516812F, 8.125F, 6F);
		builder.addVertex(0.99875F, 1.841161F, -0.021224F, 8.125F, 4.875F);

		//connection_left
		builder.addVertex(0.732982F, 1.52515F, 0.40625F, 6.375F, 4F);
		builder.addVertex(1F, 1.6875F, 0.40625F, 7F, 4F);
		builder.addVertex(1.09741F, 1.527289F, 0.40625F, 7F, 4.375F);
		builder.addVertex(0.830391F, 1.364939F, 0.40625F, 6.375F, 4.375F);

		builder.addVertex(0.732982F, 1.52515F, 0.59375F, 8F, 4F);
		builder.addVertex(0.830391F, 1.364939F, 0.59375F, 8F, 4.375F);
		builder.addVertex(1.09741F, 1.527289F, 0.59375F, 7.375F, 4.375F);
		builder.addVertex(1F, 1.6875F, 0.59375F, 7.375F, 4F);

		builder.addVertex(0.830391F, 1.364939F, 0.40625F, 7F, 4F);
		builder.addVertex(1.09741F, 1.527289F, 0.40625F, 7.625F, 4F);
		builder.addVertex(1.09741F, 1.527289F, 0.59375F, 7.625F, 3.625F);
		builder.addVertex(0.830391F, 1.364939F, 0.59375F, 7F, 3.625F);

		builder.addVertex(0.732982F, 1.52515F, 0.40625F, 6.375F, 4F);
		builder.addVertex(0.732982F, 1.52515F, 0.59375F, 6.375F, 3.625F);
		builder.addVertex(1F, 1.6875F, 0.59375F, 7F, 3.625F);
		builder.addVertex(1F, 1.6875F, 0.40625F, 7F, 4F);

		builder.addVertex(1F, 1.6875F, 0.40625F, 7F, 4F);
		builder.addVertex(1F, 1.6875F, 0.59375F, 7.375F, 4F);
		builder.addVertex(1.09741F, 1.527289F, 0.59375F, 7.375F, 4.375F);
		builder.addVertex(1.09741F, 1.527289F, 0.40625F, 7F, 4.375F);

		builder.addVertex(0.732982F, 1.52515F, 0.40625F, 6.375F, 4F);
		builder.addVertex(0.830391F, 1.364939F, 0.40625F, 6.375F, 4.375F);
		builder.addVertex(0.830391F, 1.364939F, 0.59375F, 6F, 4.375F);
		builder.addVertex(0.732982F, 1.52515F, 0.59375F, 6F, 4F);

		//support_front_right1e
		builder.addVertex(-0.18625F, 1.661816F, -0.075918F, 10.125F, 4.875F);
		builder.addVertex(0.00125F, 1.661816F, -0.075918F, 10.5F, 4.875F);
		builder.addVertex(0.00125F, 1.497732F, 0.462118F, 10.5F, 6F);
		builder.addVertex(-0.18625F, 1.497732F, 0.462118F, 10.125F, 6F);

		builder.addVertex(-0.18625F, 1.841161F, -0.021224F, 11.25F, 4.875F);
		builder.addVertex(-0.18625F, 1.677078F, 0.516813F, 11.25F, 6F);
		builder.addVertex(0.00125F, 1.677078F, 0.516813F, 10.875F, 6F);
		builder.addVertex(0.00125F, 1.841161F, -0.021224F, 10.875F, 4.875F);

		builder.addVertex(-0.18625F, 1.497732F, 0.462118F, 10.5F, 4.875F);
		builder.addVertex(0.00125F, 1.497732F, 0.462118F, 10.875F, 4.875F);
		builder.addVertex(0.00125F, 1.677078F, 0.516813F, 10.875F, 4.5F);
		builder.addVertex(-0.18625F, 1.677078F, 0.516813F, 10.5F, 4.5F);

		builder.addVertex(-0.18625F, 1.661816F, -0.075918F, 10.125F, 4.875F);
		builder.addVertex(-0.18625F, 1.841161F, -0.021224F, 10.125F, 4.5F);
		builder.addVertex(0.00125F, 1.841161F, -0.021224F, 10.5F, 4.5F);
		builder.addVertex(0.00125F, 1.661816F, -0.075918F, 10.5F, 4.875F);

		builder.addVertex(0.00125F, 1.661816F, -0.075918F, 10.5F, 4.875F);
		builder.addVertex(0.00125F, 1.841161F, -0.021224F, 10.875F, 4.875F);
		builder.addVertex(0.00125F, 1.677078F, 0.516813F, 10.875F, 6F);
		builder.addVertex(0.00125F, 1.497732F, 0.462118F, 10.5F, 6F);

		builder.addVertex(-0.18625F, 1.661816F, -0.075918F, 10.125F, 4.875F);
		builder.addVertex(-0.18625F, 1.497732F, 0.462118F, 10.125F, 6F);
		builder.addVertex(-0.18625F, 1.677078F, 0.516813F, 9.75F, 6F);
		builder.addVertex(-0.18625F, 1.841161F, -0.021224F, 9.75F, 4.875F);

		//brazier_edge_front1
		builder.addVertex(-0F, 2.030846F, -0.150458F, 0.375F, 2.875F);
		builder.addVertex(1F, 2.030846F, -0.150458F, 2.375F, 2.875F);
		builder.addVertex(1F, 1.846444F, -0.184402F, 2.375F, 3.25F);
		builder.addVertex(-0F, 1.846444F, -0.184402F, 0.375F, 3.25F);

		builder.addVertex(-0F, 1.996902F, 0.033944F, 4.75F, 2.875F);
		builder.addVertex(-0F, 1.8125F, 0F, 4.75F, 3.25F);
		builder.addVertex(1F, 1.8125F, -0F, 2.75F, 3.25F);
		builder.addVertex(1F, 1.996902F, 0.033944F, 2.75F, 2.875F);

		builder.addVertex(-0F, 1.846444F, -0.184402F, 2.375F, 2.875F);
		builder.addVertex(1F, 1.846444F, -0.184402F, 4.375F, 2.875F);
		builder.addVertex(1F, 1.8125F, -0F, 4.375F, 2.5F);
		builder.addVertex(-0F, 1.8125F, 0F, 2.375F, 2.5F);

		builder.addVertex(-0F, 2.030846F, -0.150458F, 0.375F, 2.875F);
		builder.addVertex(-0F, 1.996902F, 0.033944F, 0.375F, 2.5F);
		builder.addVertex(1F, 1.996902F, 0.033944F, 2.375F, 2.5F);
		builder.addVertex(1F, 2.030846F, -0.150458F, 2.375F, 2.875F);

		builder.addVertex(1F, 2.030846F, -0.150458F, 2.375F, 2.875F);
		builder.addVertex(1F, 1.996902F, 0.033944F, 2.75F, 2.875F);
		builder.addVertex(1F, 1.8125F, -0F, 2.75F, 3.25F);
		builder.addVertex(1F, 1.846444F, -0.184402F, 2.375F, 3.25F);

		builder.addVertex(-0F, 2.030846F, -0.150458F, 0.375F, 2.875F);
		builder.addVertex(-0F, 1.846444F, -0.184402F, 0.375F, 3.25F);
		builder.addVertex(-0F, 1.8125F, 0F, 0F, 3.25F);
		builder.addVertex(-0F, 1.996902F, 0.033944F, 0F, 2.875F);

		//connection_front
		builder.addVertex(0.40625F, 1.6875F, 0F, 6.625F, 3.125F);
		builder.addVertex(0.59375F, 1.6875F, 0F, 7F, 3.125F);
		builder.addVertex(0.59375F, 1.527289F, -0.09741F, 7F, 3.5F);
		builder.addVertex(0.40625F, 1.527289F, -0.09741F, 6.625F, 3.5F);

		builder.addVertex(0.40625F, 1.52515F, 0.267018F, 8F, 3.125F);
		builder.addVertex(0.40625F, 1.364939F, 0.169609F, 8F, 3.5F);
		builder.addVertex(0.59375F, 1.364939F, 0.169609F, 7.625F, 3.5F);
		builder.addVertex(0.59375F, 1.52515F, 0.267018F, 7.625F, 3.125F);

		builder.addVertex(0.40625F, 1.527289F, -0.09741F, 7F, 3.125F);
		builder.addVertex(0.59375F, 1.527289F, -0.09741F, 7.375F, 3.125F);
		builder.addVertex(0.59375F, 1.364939F, 0.169609F, 7.375F, 2.5F);
		builder.addVertex(0.40625F, 1.364939F, 0.169609F, 7F, 2.5F);

		builder.addVertex(0.40625F, 1.6875F, 0F, 6.625F, 3.125F);
		builder.addVertex(0.40625F, 1.52515F, 0.267018F, 6.625F, 2.5F);
		builder.addVertex(0.59375F, 1.52515F, 0.267018F, 7F, 2.5F);
		builder.addVertex(0.59375F, 1.6875F, 0F, 7F, 3.125F);

		builder.addVertex(0.59375F, 1.6875F, 0F, 7F, 3.125F);
		builder.addVertex(0.59375F, 1.52515F, 0.267018F, 7.625F, 3.125F);
		builder.addVertex(0.59375F, 1.364939F, 0.169609F, 7.625F, 3.5F);
		builder.addVertex(0.59375F, 1.527289F, -0.09741F, 7F, 3.5F);

		builder.addVertex(0.40625F, 1.6875F, 0F, 6.625F, 3.125F);
		builder.addVertex(0.40625F, 1.527289F, -0.09741F, 6.625F, 3.5F);
		builder.addVertex(0.40625F, 1.364939F, 0.169609F, 6F, 3.5F);
		builder.addVertex(0.40625F, 1.52515F, 0.267018F, 6F, 3.125F);

		//connection_right
		builder.addVertex(-0F, 1.6875F, 0.40625F, 6.375F, 6F);
		builder.addVertex(0.267018F, 1.52515F, 0.40625F, 7F, 6F);
		builder.addVertex(0.169608F, 1.364939F, 0.40625F, 7F, 6.375F);
		builder.addVertex(-0.09741F, 1.527289F, 0.40625F, 6.375F, 6.375F);

		builder.addVertex(-0F, 1.6875F, 0.59375F, 8F, 6F);
		builder.addVertex(-0.09741F, 1.527289F, 0.59375F, 8F, 6.375F);
		builder.addVertex(0.169609F, 1.364939F, 0.59375F, 7.375F, 6.375F);
		builder.addVertex(0.267018F, 1.52515F, 0.59375F, 7.375F, 6F);

		builder.addVertex(-0.09741F, 1.527289F, 0.40625F, 7F, 6F);
		builder.addVertex(0.169608F, 1.364939F, 0.40625F, 7.625F, 6F);
		builder.addVertex(0.169609F, 1.364939F, 0.59375F, 7.625F, 5.625F);
		builder.addVertex(-0.09741F, 1.527289F, 0.59375F, 7F, 5.625F);

		builder.addVertex(-0F, 1.6875F, 0.40625F, 6.375F, 6F);
		builder.addVertex(-0F, 1.6875F, 0.59375F, 6.375F, 5.625F);
		builder.addVertex(0.267018F, 1.52515F, 0.59375F, 7F, 5.625F);
		builder.addVertex(0.267018F, 1.52515F, 0.40625F, 7F, 6F);

		builder.addVertex(0.267018F, 1.52515F, 0.40625F, 7F, 6F);
		builder.addVertex(0.267018F, 1.52515F, 0.59375F, 7.375F, 6F);
		builder.addVertex(0.169609F, 1.364939F, 0.59375F, 7.375F, 6.375F);
		builder.addVertex(0.169608F, 1.364939F, 0.40625F, 7F, 6.375F);

		builder.addVertex(-0F, 1.6875F, 0.40625F, 6.375F, 6F);
		builder.addVertex(-0.09741F, 1.527289F, 0.40625F, 6.375F, 6.375F);
		builder.addVertex(-0.09741F, 1.527289F, 0.59375F, 6F, 6.375F);
		builder.addVertex(-0F, 1.6875F, 0.59375F, 6F, 6F);

		//brazier_edge_right1
		builder.addVertex(-0.150458F, 2.030846F, 0F, 2F, 13F);
		builder.addVertex(0.033944F, 1.996902F, 0F, 2.375F, 13F);
		builder.addVertex(-0F, 1.8125F, 0F, 2.375F, 13.375F);
		builder.addVertex(-0.184402F, 1.846444F, 0F, 2F, 13.375F);

		builder.addVertex(-0.150458F, 2.030846F, 1F, 4.75F, 13F);
		builder.addVertex(-0.184402F, 1.846444F, 1F, 4.75F, 13.375F);
		builder.addVertex(0F, 1.8125F, 1F, 4.375F, 13.375F);
		builder.addVertex(0.033944F, 1.996902F, 1F, 4.375F, 13F);

		builder.addVertex(-0.184402F, 1.846444F, 0F, 2.375F, 13F);
		builder.addVertex(-0F, 1.8125F, 0F, 2.75F, 13F);
		builder.addVertex(0F, 1.8125F, 1F, 2.75F, 11F);
		builder.addVertex(-0.184402F, 1.846444F, 1F, 2.375F, 11F);

		builder.addVertex(-0.150458F, 2.030846F, 0F, 2F, 13F);
		builder.addVertex(-0.150458F, 2.030846F, 1F, 2F, 11F);
		builder.addVertex(0.033944F, 1.996902F, 1F, 2.375F, 11F);
		builder.addVertex(0.033944F, 1.996902F, 0F, 2.375F, 13F);

		builder.addVertex(0.033944F, 1.996902F, 0F, 2.375F, 13F);
		builder.addVertex(0.033944F, 1.996902F, 1F, 4.375F, 13F);
		builder.addVertex(0F, 1.8125F, 1F, 4.375F, 13.375F);
		builder.addVertex(-0F, 1.8125F, 0F, 2.375F, 13.375F);

		builder.addVertex(-0.150458F, 2.030846F, 0F, 2F, 13F);
		builder.addVertex(-0.184402F, 1.846444F, 0F, 2F, 13.375F);
		builder.addVertex(-0.184402F, 1.846444F, 1F, 0F, 13.375F);
		builder.addVertex(-0.150458F, 2.030846F, 1F, 0F, 13F);

		//support_back_right1c
		builder.addVertex(-0.1875F, 1.8125F, 0.999375F, 13.375F, 4F);
		builder.addVertex(-0.036816F, 1.924082F, 0.999375F, 13.75F, 4F);
		builder.addVertex(0.074766F, 1.773398F, 0.999375F, 13.75F, 4.375F);
		builder.addVertex(-0.075918F, 1.661816F, 0.999375F, 13.375F, 4.375F);

		builder.addVertex(-0.1875F, 1.8125F, 1.186875F, 14.5F, 4F);
		builder.addVertex(-0.075918F, 1.661816F, 1.186875F, 14.5F, 4.375F);
		builder.addVertex(0.074766F, 1.773398F, 1.186875F, 14.125F, 4.375F);
		builder.addVertex(-0.036816F, 1.924082F, 1.186875F, 14.125F, 4F);

		builder.addVertex(-0.075918F, 1.661816F, 0.999375F, 13.75F, 4F);
		builder.addVertex(0.074766F, 1.773398F, 0.999375F, 14.125F, 4F);
		builder.addVertex(0.074766F, 1.773398F, 1.186875F, 14.125F, 3.625F);
		builder.addVertex(-0.075918F, 1.661816F, 1.186875F, 13.75F, 3.625F);

		builder.addVertex(-0.1875F, 1.8125F, 0.999375F, 13.375F, 4F);
		builder.addVertex(-0.1875F, 1.8125F, 1.186875F, 13.375F, 3.625F);
		builder.addVertex(-0.036816F, 1.924082F, 1.186875F, 13.75F, 3.625F);
		builder.addVertex(-0.036816F, 1.924082F, 0.999375F, 13.75F, 4F);

		builder.addVertex(-0.036816F, 1.924082F, 0.999375F, 13.75F, 4F);
		builder.addVertex(-0.036816F, 1.924082F, 1.186875F, 14.125F, 4F);
		builder.addVertex(0.074766F, 1.773398F, 1.186875F, 14.125F, 4.375F);
		builder.addVertex(0.074766F, 1.773398F, 0.999375F, 13.75F, 4.375F);

		builder.addVertex(-0.1875F, 1.8125F, 0.999375F, 13.375F, 4F);
		builder.addVertex(-0.075918F, 1.661816F, 0.999375F, 13.375F, 4.375F);
		builder.addVertex(-0.075918F, 1.661816F, 1.186875F, 13F, 4.375F);
		builder.addVertex(-0.1875F, 1.8125F, 1.186875F, 13F, 4F);

		//standard_front_left2
		builder.addVertex(0.623935F, 1.267032F, 0.188278F, 6.375F, 11.25F);
		builder.addVertex(0.810656F, 1.284091F, 0.188769F, 6.75F, 11.25F);
		builder.addVertex(0.873109F, 0.602297F, 0.126221F, 6.75F, 12.625F);
		builder.addVertex(0.686387F, 0.585238F, 0.12573F, 6.375F, 12.625F);

		builder.addVertex(0.625F, 1.25F, 0.375F, 7.5F, 11.25F);
		builder.addVertex(0.687452F, 0.568206F, 0.312452F, 7.5F, 12.625F);
		builder.addVertex(0.874174F, 0.585264F, 0.312942F, 7.125F, 12.625F);
		builder.addVertex(0.811722F, 1.267059F, 0.375491F, 7.125F, 11.25F);

		builder.addVertex(0.686387F, 0.585238F, 0.12573F, 6.75F, 11.25F);
		builder.addVertex(0.873109F, 0.602297F, 0.126221F, 7.125F, 11.25F);
		builder.addVertex(0.874174F, 0.585264F, 0.312942F, 7.125F, 10.875F);
		builder.addVertex(0.687452F, 0.568206F, 0.312452F, 6.75F, 10.875F);

		builder.addVertex(0.623935F, 1.267032F, 0.188278F, 6.375F, 11.25F);
		builder.addVertex(0.625F, 1.25F, 0.375F, 6.375F, 10.875F);
		builder.addVertex(0.811722F, 1.267059F, 0.375491F, 6.75F, 10.875F);
		builder.addVertex(0.810656F, 1.284091F, 0.188769F, 6.75F, 11.25F);

		builder.addVertex(0.810656F, 1.284091F, 0.188769F, 6.75F, 11.25F);
		builder.addVertex(0.811722F, 1.267059F, 0.375491F, 7.125F, 11.25F);
		builder.addVertex(0.874174F, 0.585264F, 0.312942F, 7.125F, 12.625F);
		builder.addVertex(0.873109F, 0.602297F, 0.126221F, 6.75F, 12.625F);

		builder.addVertex(0.623935F, 1.267032F, 0.188278F, 6.375F, 11.25F);
		builder.addVertex(0.686387F, 0.585238F, 0.12573F, 6.375F, 12.625F);
		builder.addVertex(0.687452F, 0.568206F, 0.312452F, 6F, 12.625F);
		builder.addVertex(0.625F, 1.25F, 0.375F, 6F, 11.25F);

		//standardrope_back_right1
		builder.addVertex(0.159702F, 1.329451F, 0.590589F, 6.5F, 15.125F);
		builder.addVertex(0.40944F, 1.318019F, 0.590207F, 7F, 15.125F);
		builder.addVertex(0.403739F, 1.193279F, 0.595902F, 7F, 15.375F);
		builder.addVertex(0.154001F, 1.204711F, 0.596284F, 6.5F, 15.375F);

		builder.addVertex(0.160604F, 1.340812F, 0.840329F, 8F, 15.125F);
		builder.addVertex(0.154903F, 1.216072F, 0.846024F, 8F, 15.375F);
		builder.addVertex(0.404641F, 1.204641F, 0.845642F, 7.5F, 15.375F);
		builder.addVertex(0.410342F, 1.32938F, 0.839947F, 7.5F, 15.125F);

		builder.addVertex(0.154001F, 1.204711F, 0.596284F, 7F, 15.125F);
		builder.addVertex(0.403739F, 1.193279F, 0.595902F, 7.5F, 15.125F);
		builder.addVertex(0.404641F, 1.204641F, 0.845642F, 7.5F, 14.625F);
		builder.addVertex(0.154903F, 1.216072F, 0.846024F, 7F, 14.625F);

		builder.addVertex(0.159702F, 1.329451F, 0.590589F, 6.5F, 15.125F);
		builder.addVertex(0.160604F, 1.340812F, 0.840329F, 6.5F, 14.625F);
		builder.addVertex(0.410342F, 1.32938F, 0.839947F, 7F, 14.625F);
		builder.addVertex(0.40944F, 1.318019F, 0.590207F, 7F, 15.125F);

		builder.addVertex(0.40944F, 1.318019F, 0.590207F, 7F, 15.125F);
		builder.addVertex(0.410342F, 1.32938F, 0.839947F, 7.5F, 15.125F);
		builder.addVertex(0.404641F, 1.204641F, 0.845642F, 7.5F, 15.375F);
		builder.addVertex(0.403739F, 1.193279F, 0.595902F, 7F, 15.375F);

		builder.addVertex(0.159702F, 1.329451F, 0.590589F, 6.5F, 15.125F);
		builder.addVertex(0.154001F, 1.204711F, 0.596284F, 6.5F, 15.375F);
		builder.addVertex(0.154903F, 1.216072F, 0.846024F, 6F, 15.375F);
		builder.addVertex(0.160604F, 1.340812F, 0.840329F, 6F, 15.125F);

		//standard_front_right3
		builder.addVertex(0.131816F, 0.635822F, 0.130581F, 8F, 13.125F);
		builder.addVertex(0.316216F, 0.601962F, 0.128052F, 8.375F, 13.125F);
		builder.addVertex(0.192444F, -0.062811F, 0.003897F, 8.375F, 14.5F);
		builder.addVertex(0.008044F, -0.02895F, 0.006426F, 8F, 14.5F);

		builder.addVertex(0.128147F, 0.602066F, 0.314981F, 9.125F, 13.125F);
		builder.addVertex(0.004375F, -0.062706F, 0.190826F, 9.125F, 14.5F);
		builder.addVertex(0.188775F, -0.096567F, 0.188297F, 8.75F, 14.5F);
		builder.addVertex(0.312547F, 0.568206F, 0.312452F, 8.75F, 13.125F);

		builder.addVertex(0.008044F, -0.02895F, 0.006426F, 8.375F, 13.125F);
		builder.addVertex(0.192444F, -0.062811F, 0.003897F, 8.75F, 13.125F);
		builder.addVertex(0.188775F, -0.096567F, 0.188297F, 8.75F, 12.75F);
		builder.addVertex(0.004375F, -0.062706F, 0.190826F, 8.375F, 12.75F);

		builder.addVertex(0.131816F, 0.635822F, 0.130581F, 8F, 13.125F);
		builder.addVertex(0.128147F, 0.602066F, 0.314981F, 8F, 12.75F);
		builder.addVertex(0.312547F, 0.568206F, 0.312452F, 8.375F, 12.75F);
		builder.addVertex(0.316216F, 0.601962F, 0.128052F, 8.375F, 13.125F);

		builder.addVertex(0.316216F, 0.601962F, 0.128052F, 8.375F, 13.125F);
		builder.addVertex(0.312547F, 0.568206F, 0.312452F, 8.75F, 13.125F);
		builder.addVertex(0.188775F, -0.096567F, 0.188297F, 8.75F, 14.5F);
		builder.addVertex(0.192444F, -0.062811F, 0.003897F, 8.375F, 14.5F);

		builder.addVertex(0.131816F, 0.635822F, 0.130581F, 8F, 13.125F);
		builder.addVertex(0.008044F, -0.02895F, 0.006426F, 8F, 14.5F);
		builder.addVertex(0.004375F, -0.062706F, 0.190826F, 7.625F, 14.5F);
		builder.addVertex(0.128147F, 0.602066F, 0.314981F, 7.625F, 13.125F);

		//standard_back_right1
		builder.addVertex(0.1875F, 1.8125F, 0.625F, 11.25F, 9.625F);
		builder.addVertex(0.375F, 1.8125F, 0.625F, 11.625F, 9.625F);
		builder.addVertex(0.375F, 1.25F, 0.625F, 11.625F, 10.75F);
		builder.addVertex(0.1875F, 1.25F, 0.625F, 11.25F, 10.75F);

		builder.addVertex(0.1875F, 1.8125F, 0.8125F, 12.375F, 9.625F);
		builder.addVertex(0.1875F, 1.25F, 0.8125F, 12.375F, 10.75F);
		builder.addVertex(0.375F, 1.25F, 0.8125F, 12F, 10.75F);
		builder.addVertex(0.375F, 1.8125F, 0.8125F, 12F, 9.625F);

		builder.addVertex(0.1875F, 1.25F, 0.625F, 11.625F, 9.625F);
		builder.addVertex(0.375F, 1.25F, 0.625F, 12F, 9.625F);
		builder.addVertex(0.375F, 1.25F, 0.8125F, 12F, 9.25F);
		builder.addVertex(0.1875F, 1.25F, 0.8125F, 11.625F, 9.25F);

		builder.addVertex(0.1875F, 1.8125F, 0.625F, 11.25F, 9.625F);
		builder.addVertex(0.1875F, 1.8125F, 0.8125F, 11.25F, 9.25F);
		builder.addVertex(0.375F, 1.8125F, 0.8125F, 11.625F, 9.25F);
		builder.addVertex(0.375F, 1.8125F, 0.625F, 11.625F, 9.625F);

		builder.addVertex(0.375F, 1.8125F, 0.625F, 11.625F, 9.625F);
		builder.addVertex(0.375F, 1.8125F, 0.8125F, 12F, 9.625F);
		builder.addVertex(0.375F, 1.25F, 0.8125F, 12F, 10.75F);
		builder.addVertex(0.375F, 1.25F, 0.625F, 11.625F, 10.75F);

		builder.addVertex(0.1875F, 1.8125F, 0.625F, 11.25F, 9.625F);
		builder.addVertex(0.1875F, 1.25F, 0.625F, 11.25F, 10.75F);
		builder.addVertex(0.1875F, 1.25F, 0.8125F, 10.875F, 10.75F);
		builder.addVertex(0.1875F, 1.8125F, 0.8125F, 10.875F, 9.625F);

		//rope_back
		builder.addVertex(0.4375F, 1.71875F, 0.96875F, 13.5F, 6.625F);
		builder.addVertex(0.5625F, 1.71875F, 0.96875F, 13.75F, 6.625F);
		builder.addVertex(0.5625F, 1.46875F, 0.96875F, 13.75F, 7.125F);
		builder.addVertex(0.4375F, 1.46875F, 0.96875F, 13.5F, 7.125F);

		builder.addVertex(0.4375F, 1.71875F, 1.21875F, 14.5F, 6.625F);
		builder.addVertex(0.4375F, 1.46875F, 1.21875F, 14.5F, 7.125F);
		builder.addVertex(0.5625F, 1.46875F, 1.21875F, 14.25F, 7.125F);
		builder.addVertex(0.5625F, 1.71875F, 1.21875F, 14.25F, 6.625F);

		builder.addVertex(0.4375F, 1.46875F, 0.96875F, 13.75F, 6.625F);
		builder.addVertex(0.5625F, 1.46875F, 0.96875F, 14F, 6.625F);
		builder.addVertex(0.5625F, 1.46875F, 1.21875F, 14F, 6.125F);
		builder.addVertex(0.4375F, 1.46875F, 1.21875F, 13.75F, 6.125F);

		builder.addVertex(0.4375F, 1.71875F, 0.96875F, 13.5F, 6.625F);
		builder.addVertex(0.4375F, 1.71875F, 1.21875F, 13.5F, 6.125F);
		builder.addVertex(0.5625F, 1.71875F, 1.21875F, 13.75F, 6.125F);
		builder.addVertex(0.5625F, 1.71875F, 0.96875F, 13.75F, 6.625F);

		builder.addVertex(0.5625F, 1.71875F, 0.96875F, 13.75F, 6.625F);
		builder.addVertex(0.5625F, 1.71875F, 1.21875F, 14.25F, 6.625F);
		builder.addVertex(0.5625F, 1.46875F, 1.21875F, 14.25F, 7.125F);
		builder.addVertex(0.5625F, 1.46875F, 0.96875F, 13.75F, 7.125F);

		builder.addVertex(0.4375F, 1.71875F, 0.96875F, 13.5F, 6.625F);
		builder.addVertex(0.4375F, 1.46875F, 0.96875F, 13.5F, 7.125F);
		builder.addVertex(0.4375F, 1.46875F, 1.21875F, 13F, 7.125F);
		builder.addVertex(0.4375F, 1.71875F, 1.21875F, 13F, 6.625F);

		//support_back_left1a
		builder.addVertex(1F, 2.125F, 1F, 11.75F, 0.375F);
		builder.addVertex(1.1875F, 2.125F, 1F, 12.125F, 0.375F);
		builder.addVertex(1.1875F, 1.8125F, 1F, 12.125F, 1F);
		builder.addVertex(1F, 1.8125F, 1F, 11.75F, 1F);

		builder.addVertex(1F, 2.125F, 1.1875F, 12.875F, 0.375F);
		builder.addVertex(1F, 1.8125F, 1.1875F, 12.875F, 1F);
		builder.addVertex(1.1875F, 1.8125F, 1.1875F, 12.5F, 1F);
		builder.addVertex(1.1875F, 2.125F, 1.1875F, 12.5F, 0.375F);

		builder.addVertex(1F, 1.8125F, 1F, 12.125F, 0.375F);
		builder.addVertex(1.1875F, 1.8125F, 1F, 12.5F, 0.375F);
		builder.addVertex(1.1875F, 1.8125F, 1.1875F, 12.5F, 0F);
		builder.addVertex(1F, 1.8125F, 1.1875F, 12.125F, 0F);

		builder.addVertex(1F, 2.125F, 1F, 11.75F, 0.375F);
		builder.addVertex(1F, 2.125F, 1.1875F, 11.75F, 0F);
		builder.addVertex(1.1875F, 2.125F, 1.1875F, 12.125F, 0F);
		builder.addVertex(1.1875F, 2.125F, 1F, 12.125F, 0.375F);

		builder.addVertex(1.1875F, 2.125F, 1F, 12.125F, 0.375F);
		builder.addVertex(1.1875F, 2.125F, 1.1875F, 12.5F, 0.375F);
		builder.addVertex(1.1875F, 1.8125F, 1.1875F, 12.5F, 1F);
		builder.addVertex(1.1875F, 1.8125F, 1F, 12.125F, 1F);

		builder.addVertex(1F, 2.125F, 1F, 11.75F, 0.375F);
		builder.addVertex(1F, 1.8125F, 1F, 11.75F, 1F);
		builder.addVertex(1F, 1.8125F, 1.1875F, 11.375F, 1F);
		builder.addVertex(1F, 2.125F, 1.1875F, 11.375F, 0.375F);

		//support_back_right1d
		builder.addVertex(-0.18625F, 1.841161F, 1.021224F, 13.375F, 2.375F);
		builder.addVertex(0.00125F, 1.841161F, 1.021224F, 13.75F, 2.375F);
		builder.addVertex(0.00125F, 1.677078F, 0.483188F, 13.75F, 3.5F);
		builder.addVertex(-0.18625F, 1.677078F, 0.483188F, 13.375F, 3.5F);

		builder.addVertex(-0.18625F, 1.661816F, 1.075918F, 14.5F, 2.375F);
		builder.addVertex(-0.18625F, 1.497732F, 0.537882F, 14.5F, 3.5F);
		builder.addVertex(0.00125F, 1.497732F, 0.537882F, 14.125F, 3.5F);
		builder.addVertex(0.00125F, 1.661816F, 1.075918F, 14.125F, 2.375F);

		builder.addVertex(-0.18625F, 1.677078F, 0.483188F, 13.75F, 2.375F);
		builder.addVertex(0.00125F, 1.677078F, 0.483188F, 14.125F, 2.375F);
		builder.addVertex(0.00125F, 1.497732F, 0.537882F, 14.125F, 2F);
		builder.addVertex(-0.18625F, 1.497732F, 0.537882F, 13.75F, 2F);

		builder.addVertex(-0.18625F, 1.841161F, 1.021224F, 13.375F, 2.375F);
		builder.addVertex(-0.18625F, 1.661816F, 1.075918F, 13.375F, 2F);
		builder.addVertex(0.00125F, 1.661816F, 1.075918F, 13.75F, 2F);
		builder.addVertex(0.00125F, 1.841161F, 1.021224F, 13.75F, 2.375F);

		builder.addVertex(0.00125F, 1.841161F, 1.021224F, 13.75F, 2.375F);
		builder.addVertex(0.00125F, 1.661816F, 1.075918F, 14.125F, 2.375F);
		builder.addVertex(0.00125F, 1.497732F, 0.537882F, 14.125F, 3.5F);
		builder.addVertex(0.00125F, 1.677078F, 0.483188F, 13.75F, 3.5F);

		builder.addVertex(-0.18625F, 1.841161F, 1.021224F, 13.375F, 2.375F);
		builder.addVertex(-0.18625F, 1.677078F, 0.483188F, 13.375F, 3.5F);
		builder.addVertex(-0.18625F, 1.497732F, 0.537882F, 13F, 3.5F);
		builder.addVertex(-0.18625F, 1.661816F, 1.075918F, 13F, 2.375F);

		//standardrope_front_left1
		builder.addVertex(0.589657F, 1.32938F, 0.160053F, 6.5F, 15.125F);
		builder.addVertex(0.839396F, 1.340812F, 0.159671F, 7F, 15.125F);
		builder.addVertex(0.845097F, 1.216072F, 0.153976F, 7F, 15.375F);
		builder.addVertex(0.595359F, 1.20464F, 0.154358F, 6.5F, 15.375F);

		builder.addVertex(0.59056F, 1.318019F, 0.409793F, 8F, 15.125F);
		builder.addVertex(0.596261F, 1.193279F, 0.404098F, 8F, 15.375F);
		builder.addVertex(0.845999F, 1.204711F, 0.403716F, 7.5F, 15.375F);
		builder.addVertex(0.840298F, 1.329451F, 0.409411F, 7.5F, 15.125F);

		builder.addVertex(0.595359F, 1.20464F, 0.154358F, 7F, 15.125F);
		builder.addVertex(0.845097F, 1.216072F, 0.153976F, 7.5F, 15.125F);
		builder.addVertex(0.845999F, 1.204711F, 0.403716F, 7.5F, 14.625F);
		builder.addVertex(0.596261F, 1.193279F, 0.404098F, 7F, 14.625F);

		builder.addVertex(0.589657F, 1.32938F, 0.160053F, 6.5F, 15.125F);
		builder.addVertex(0.59056F, 1.318019F, 0.409793F, 6.5F, 14.625F);
		builder.addVertex(0.840298F, 1.329451F, 0.409411F, 7F, 14.625F);
		builder.addVertex(0.839396F, 1.340812F, 0.159671F, 7F, 15.125F);

		builder.addVertex(0.839396F, 1.340812F, 0.159671F, 7F, 15.125F);
		builder.addVertex(0.840298F, 1.329451F, 0.409411F, 7.5F, 15.125F);
		builder.addVertex(0.845999F, 1.204711F, 0.403716F, 7.5F, 15.375F);
		builder.addVertex(0.845097F, 1.216072F, 0.153976F, 7F, 15.375F);

		builder.addVertex(0.589657F, 1.32938F, 0.160053F, 6.5F, 15.125F);
		builder.addVertex(0.595359F, 1.20464F, 0.154358F, 6.5F, 15.375F);
		builder.addVertex(0.596261F, 1.193279F, 0.404098F, 6F, 15.375F);
		builder.addVertex(0.59056F, 1.318019F, 0.409793F, 6F, 15.125F);

		//support_back_left1e
		builder.addVertex(1.021224F, 1.841161F, 0.99875F, 11.75F, 4.875F);
		builder.addVertex(1.075918F, 1.661816F, 0.99875F, 12.125F, 4.875F);
		builder.addVertex(0.537882F, 1.497732F, 0.99875F, 12.125F, 6F);
		builder.addVertex(0.483188F, 1.677078F, 0.99875F, 11.75F, 6F);

		builder.addVertex(1.021224F, 1.841161F, 1.18625F, 12.875F, 4.875F);
		builder.addVertex(0.483188F, 1.677078F, 1.18625F, 12.875F, 6F);
		builder.addVertex(0.537882F, 1.497732F, 1.18625F, 12.5F, 6F);
		builder.addVertex(1.075918F, 1.661816F, 1.18625F, 12.5F, 4.875F);

		builder.addVertex(0.483188F, 1.677078F, 0.99875F, 12.125F, 4.875F);
		builder.addVertex(0.537882F, 1.497732F, 0.99875F, 12.5F, 4.875F);
		builder.addVertex(0.537882F, 1.497732F, 1.18625F, 12.5F, 4.5F);
		builder.addVertex(0.483188F, 1.677078F, 1.18625F, 12.125F, 4.5F);

		builder.addVertex(1.021224F, 1.841161F, 0.99875F, 11.75F, 4.875F);
		builder.addVertex(1.021224F, 1.841161F, 1.18625F, 11.75F, 4.5F);
		builder.addVertex(1.075918F, 1.661816F, 1.18625F, 12.125F, 4.5F);
		builder.addVertex(1.075918F, 1.661816F, 0.99875F, 12.125F, 4.875F);

		builder.addVertex(1.075918F, 1.661816F, 0.99875F, 12.125F, 4.875F);
		builder.addVertex(1.075918F, 1.661816F, 1.18625F, 12.5F, 4.875F);
		builder.addVertex(0.537882F, 1.497732F, 1.18625F, 12.5F, 6F);
		builder.addVertex(0.537882F, 1.497732F, 0.99875F, 12.125F, 6F);

		builder.addVertex(1.021224F, 1.841161F, 0.99875F, 11.75F, 4.875F);
		builder.addVertex(0.483188F, 1.677078F, 0.99875F, 11.75F, 6F);
		builder.addVertex(0.483188F, 1.677078F, 1.18625F, 11.375F, 6F);
		builder.addVertex(1.021224F, 1.841161F, 1.18625F, 11.375F, 4.875F);

		//support_front_right1d
		builder.addVertex(-0.075918F, 1.661816F, -0.18625F, 10.125F, 2.375F);
		builder.addVertex(-0.021224F, 1.841161F, -0.18625F, 10.5F, 2.375F);
		builder.addVertex(0.516812F, 1.677078F, -0.18625F, 10.5F, 3.5F);
		builder.addVertex(0.462118F, 1.497732F, -0.18625F, 10.125F, 3.5F);

		builder.addVertex(-0.075918F, 1.661816F, 0.00125F, 11.25F, 2.375F);
		builder.addVertex(0.462118F, 1.497732F, 0.00125F, 11.25F, 3.5F);
		builder.addVertex(0.516812F, 1.677078F, 0.00125F, 10.875F, 3.5F);
		builder.addVertex(-0.021224F, 1.841161F, 0.00125F, 10.875F, 2.375F);

		builder.addVertex(0.462118F, 1.497732F, -0.18625F, 10.5F, 2.375F);
		builder.addVertex(0.516812F, 1.677078F, -0.18625F, 10.875F, 2.375F);
		builder.addVertex(0.516812F, 1.677078F, 0.00125F, 10.875F, 2F);
		builder.addVertex(0.462118F, 1.497732F, 0.00125F, 10.5F, 2F);

		builder.addVertex(-0.075918F, 1.661816F, -0.18625F, 10.125F, 2.375F);
		builder.addVertex(-0.075918F, 1.661816F, 0.00125F, 10.125F, 2F);
		builder.addVertex(-0.021224F, 1.841161F, 0.00125F, 10.5F, 2F);
		builder.addVertex(-0.021224F, 1.841161F, -0.18625F, 10.5F, 2.375F);

		builder.addVertex(-0.021224F, 1.841161F, -0.18625F, 10.5F, 2.375F);
		builder.addVertex(-0.021224F, 1.841161F, 0.00125F, 10.875F, 2.375F);
		builder.addVertex(0.516812F, 1.677078F, 0.00125F, 10.875F, 3.5F);
		builder.addVertex(0.516812F, 1.677078F, -0.18625F, 10.5F, 3.5F);

		builder.addVertex(-0.075918F, 1.661816F, -0.18625F, 10.125F, 2.375F);
		builder.addVertex(0.462118F, 1.497732F, -0.18625F, 10.125F, 3.5F);
		builder.addVertex(0.462118F, 1.497732F, 0.00125F, 9.75F, 3.5F);
		builder.addVertex(-0.075918F, 1.661816F, 0.00125F, 9.75F, 2.375F);

		//brazier_edge_left2
		builder.addVertex(1.086992F, 2.006655F, -0F, 2F, 8.75F);
		builder.addVertex(1.247203F, 2.104065F, -0F, 2.375F, 8.75F);
		builder.addVertex(1.344613F, 1.943854F, -0F, 2.375F, 9.125F);
		builder.addVertex(1.184402F, 1.846444F, -0F, 2F, 9.125F);

		builder.addVertex(1.086992F, 2.006655F, 1F, 4.75F, 8.75F);
		builder.addVertex(1.184402F, 1.846444F, 1F, 4.75F, 9.125F);
		builder.addVertex(1.344613F, 1.943854F, 1F, 4.375F, 9.125F);
		builder.addVertex(1.247203F, 2.104065F, 1F, 4.375F, 8.75F);

		builder.addVertex(1.184402F, 1.846444F, -0F, 2.375F, 8.75F);
		builder.addVertex(1.344613F, 1.943854F, -0F, 2.75F, 8.75F);
		builder.addVertex(1.344613F, 1.943854F, 1F, 2.75F, 6.75F);
		builder.addVertex(1.184402F, 1.846444F, 1F, 2.375F, 6.75F);

		builder.addVertex(1.086992F, 2.006655F, -0F, 2F, 8.75F);
		builder.addVertex(1.086992F, 2.006655F, 1F, 2F, 6.75F);
		builder.addVertex(1.247203F, 2.104065F, 1F, 2.375F, 6.75F);
		builder.addVertex(1.247203F, 2.104065F, -0F, 2.375F, 8.75F);

		builder.addVertex(1.247203F, 2.104065F, -0F, 2.375F, 8.75F);
		builder.addVertex(1.247203F, 2.104065F, 1F, 4.375F, 8.75F);
		builder.addVertex(1.344613F, 1.943854F, 1F, 4.375F, 9.125F);
		builder.addVertex(1.344613F, 1.943854F, -0F, 2.375F, 9.125F);

		builder.addVertex(1.086992F, 2.006655F, -0F, 2F, 8.75F);
		builder.addVertex(1.184402F, 1.846444F, -0F, 2F, 9.125F);
		builder.addVertex(1.184402F, 1.846444F, 1F, 0F, 9.125F);
		builder.addVertex(1.086992F, 2.006655F, 1F, 0F, 8.75F);

		//support_back_right1e
		builder.addVertex(-0.075918F, 1.661816F, 0.99875F, 13.375F, 4.875F);
		builder.addVertex(-0.021224F, 1.841161F, 0.99875F, 13.75F, 4.875F);
		builder.addVertex(0.516813F, 1.677078F, 0.99875F, 13.75F, 6F);
		builder.addVertex(0.462118F, 1.497732F, 0.99875F, 13.375F, 6F);

		builder.addVertex(-0.075918F, 1.661816F, 1.18625F, 14.5F, 4.875F);
		builder.addVertex(0.462118F, 1.497732F, 1.18625F, 14.5F, 6F);
		builder.addVertex(0.516813F, 1.677078F, 1.18625F, 14.125F, 6F);
		builder.addVertex(-0.021224F, 1.841161F, 1.18625F, 14.125F, 4.875F);

		builder.addVertex(0.462118F, 1.497732F, 0.99875F, 13.75F, 4.875F);
		builder.addVertex(0.516813F, 1.677078F, 0.99875F, 14.125F, 4.875F);
		builder.addVertex(0.516813F, 1.677078F, 1.18625F, 14.125F, 4.5F);
		builder.addVertex(0.462118F, 1.497732F, 1.18625F, 13.75F, 4.5F);

		builder.addVertex(-0.075918F, 1.661816F, 0.99875F, 13.375F, 4.875F);
		builder.addVertex(-0.075918F, 1.661816F, 1.18625F, 13.375F, 4.5F);
		builder.addVertex(-0.021224F, 1.841161F, 1.18625F, 13.75F, 4.5F);
		builder.addVertex(-0.021224F, 1.841161F, 0.99875F, 13.75F, 4.875F);

		builder.addVertex(-0.021224F, 1.841161F, 0.99875F, 13.75F, 4.875F);
		builder.addVertex(-0.021224F, 1.841161F, 1.18625F, 14.125F, 4.875F);
		builder.addVertex(0.516813F, 1.677078F, 1.18625F, 14.125F, 6F);
		builder.addVertex(0.516813F, 1.677078F, 0.99875F, 13.75F, 6F);

		builder.addVertex(-0.075918F, 1.661816F, 0.99875F, 13.375F, 4.875F);
		builder.addVertex(0.462118F, 1.497732F, 0.99875F, 13.375F, 6F);
		builder.addVertex(0.462118F, 1.497732F, 1.18625F, 13F, 6F);
		builder.addVertex(-0.075918F, 1.661816F, 1.18625F, 13F, 4.875F);

		//standardrope_back_left2
		builder.addVertex(0.647174F, 0.629366F, 0.646802F, 6.5F, 15.125F);
		builder.addVertex(0.894835F, 0.663459F, 0.645488F, 7F, 15.125F);
		builder.addVertex(0.911811F, 0.54079F, 0.662494F, 7F, 15.375F);
		builder.addVertex(0.66415F, 0.506698F, 0.663807F, 6.5F, 15.375F);

		builder.addVertex(0.643825F, 0.663238F, 0.894474F, 8F, 15.125F);
		builder.addVertex(0.660801F, 0.540569F, 0.91148F, 8F, 15.375F);
		builder.addVertex(0.908462F, 0.574661F, 0.910166F, 7.5F, 15.375F);
		builder.addVertex(0.891486F, 0.69733F, 0.893161F, 7.5F, 15.125F);

		builder.addVertex(0.66415F, 0.506698F, 0.663807F, 7F, 15.125F);
		builder.addVertex(0.911811F, 0.54079F, 0.662494F, 7.5F, 15.125F);
		builder.addVertex(0.908462F, 0.574661F, 0.910166F, 7.5F, 14.625F);
		builder.addVertex(0.660801F, 0.540569F, 0.91148F, 7F, 14.625F);

		builder.addVertex(0.647174F, 0.629366F, 0.646802F, 6.5F, 15.125F);
		builder.addVertex(0.643825F, 0.663238F, 0.894474F, 6.5F, 14.625F);
		builder.addVertex(0.891486F, 0.69733F, 0.893161F, 7F, 14.625F);
		builder.addVertex(0.894835F, 0.663459F, 0.645488F, 7F, 15.125F);

		builder.addVertex(0.894835F, 0.663459F, 0.645488F, 7F, 15.125F);
		builder.addVertex(0.891486F, 0.69733F, 0.893161F, 7.5F, 15.125F);
		builder.addVertex(0.908462F, 0.574661F, 0.910166F, 7.5F, 15.375F);
		builder.addVertex(0.911811F, 0.54079F, 0.662494F, 7F, 15.375F);

		builder.addVertex(0.647174F, 0.629366F, 0.646802F, 6.5F, 15.125F);
		builder.addVertex(0.66415F, 0.506698F, 0.663807F, 6.5F, 15.375F);
		builder.addVertex(0.660801F, 0.540569F, 0.91148F, 6F, 15.375F);
		builder.addVertex(0.643825F, 0.663238F, 0.894474F, 6F, 15.125F);

		//support_front_left1c
		builder.addVertex(0.999375F, 1.8125F, -0.1875F, 8.5F, 4F);
		builder.addVertex(1.186875F, 1.8125F, -0.1875F, 8.875F, 4F);
		builder.addVertex(1.186875F, 1.661816F, -0.075918F, 8.875F, 4.375F);
		builder.addVertex(0.999375F, 1.661816F, -0.075918F, 8.5F, 4.375F);

		builder.addVertex(0.999375F, 1.924082F, -0.036816F, 9.625F, 4F);
		builder.addVertex(0.999375F, 1.773398F, 0.074766F, 9.625F, 4.375F);
		builder.addVertex(1.186875F, 1.773398F, 0.074766F, 9.25F, 4.375F);
		builder.addVertex(1.186875F, 1.924082F, -0.036816F, 9.25F, 4F);

		builder.addVertex(0.999375F, 1.661816F, -0.075918F, 8.875F, 4F);
		builder.addVertex(1.186875F, 1.661816F, -0.075918F, 9.25F, 4F);
		builder.addVertex(1.186875F, 1.773398F, 0.074766F, 9.25F, 3.625F);
		builder.addVertex(0.999375F, 1.773398F, 0.074766F, 8.875F, 3.625F);

		builder.addVertex(0.999375F, 1.8125F, -0.1875F, 8.5F, 4F);
		builder.addVertex(0.999375F, 1.924082F, -0.036816F, 8.5F, 3.625F);
		builder.addVertex(1.186875F, 1.924082F, -0.036816F, 8.875F, 3.625F);
		builder.addVertex(1.186875F, 1.8125F, -0.1875F, 8.875F, 4F);

		builder.addVertex(1.186875F, 1.8125F, -0.1875F, 8.875F, 4F);
		builder.addVertex(1.186875F, 1.924082F, -0.036816F, 9.25F, 4F);
		builder.addVertex(1.186875F, 1.773398F, 0.074766F, 9.25F, 4.375F);
		builder.addVertex(1.186875F, 1.661816F, -0.075918F, 8.875F, 4.375F);

		builder.addVertex(0.999375F, 1.8125F, -0.1875F, 8.5F, 4F);
		builder.addVertex(0.999375F, 1.661816F, -0.075918F, 8.5F, 4.375F);
		builder.addVertex(0.999375F, 1.773398F, 0.074766F, 8.125F, 4.375F);
		builder.addVertex(0.999375F, 1.924082F, -0.036816F, 8.125F, 4F);

		//standard_front_left3
		builder.addVertex(0.683783F, 0.601962F, 0.128052F, 6.375F, 13.125F);
		builder.addVertex(0.868183F, 0.635822F, 0.130581F, 6.75F, 13.125F);
		builder.addVertex(0.991955F, -0.02895F, 0.006426F, 6.75F, 14.5F);
		builder.addVertex(0.807556F, -0.062811F, 0.003897F, 6.375F, 14.5F);

		builder.addVertex(0.687452F, 0.568206F, 0.312452F, 7.5F, 13.125F);
		builder.addVertex(0.811224F, -0.096567F, 0.188296F, 7.5F, 14.5F);
		builder.addVertex(0.995624F, -0.062706F, 0.190826F, 7.125F, 14.5F);
		builder.addVertex(0.871852F, 0.602066F, 0.314981F, 7.125F, 13.125F);

		builder.addVertex(0.807556F, -0.062811F, 0.003897F, 6.75F, 13.125F);
		builder.addVertex(0.991955F, -0.02895F, 0.006426F, 7.125F, 13.125F);
		builder.addVertex(0.995624F, -0.062706F, 0.190826F, 7.125F, 12.75F);
		builder.addVertex(0.811224F, -0.096567F, 0.188296F, 6.75F, 12.75F);

		builder.addVertex(0.683783F, 0.601962F, 0.128052F, 6.375F, 13.125F);
		builder.addVertex(0.687452F, 0.568206F, 0.312452F, 6.375F, 12.75F);
		builder.addVertex(0.871852F, 0.602066F, 0.314981F, 6.75F, 12.75F);
		builder.addVertex(0.868183F, 0.635822F, 0.130581F, 6.75F, 13.125F);

		builder.addVertex(0.868183F, 0.635822F, 0.130581F, 6.75F, 13.125F);
		builder.addVertex(0.871852F, 0.602066F, 0.314981F, 7.125F, 13.125F);
		builder.addVertex(0.995624F, -0.062706F, 0.190826F, 7.125F, 14.5F);
		builder.addVertex(0.991955F, -0.02895F, 0.006426F, 6.75F, 14.5F);

		builder.addVertex(0.683783F, 0.601962F, 0.128052F, 6.375F, 13.125F);
		builder.addVertex(0.807556F, -0.062811F, 0.003897F, 6.375F, 14.5F);
		builder.addVertex(0.811224F, -0.096567F, 0.188296F, 6F, 14.5F);
		builder.addVertex(0.687452F, 0.568206F, 0.312452F, 6F, 13.125F);

		//support_front_left1d
		builder.addVertex(1.021224F, 1.841161F, -0.18625F, 8.5F, 2.375F);
		builder.addVertex(1.075918F, 1.661816F, -0.18625F, 8.875F, 2.375F);
		builder.addVertex(0.537882F, 1.497732F, -0.18625F, 8.875F, 3.5F);
		builder.addVertex(0.483187F, 1.677078F, -0.18625F, 8.5F, 3.5F);

		builder.addVertex(1.021224F, 1.841161F, 0.00125F, 9.625F, 2.375F);
		builder.addVertex(0.483187F, 1.677078F, 0.00125F, 9.625F, 3.5F);
		builder.addVertex(0.537882F, 1.497732F, 0.00125F, 9.25F, 3.5F);
		builder.addVertex(1.075918F, 1.661816F, 0.00125F, 9.25F, 2.375F);

		builder.addVertex(0.483187F, 1.677078F, -0.18625F, 8.875F, 2.375F);
		builder.addVertex(0.537882F, 1.497732F, -0.18625F, 9.25F, 2.375F);
		builder.addVertex(0.537882F, 1.497732F, 0.00125F, 9.25F, 2F);
		builder.addVertex(0.483187F, 1.677078F, 0.00125F, 8.875F, 2F);

		builder.addVertex(1.021224F, 1.841161F, -0.18625F, 8.5F, 2.375F);
		builder.addVertex(1.021224F, 1.841161F, 0.00125F, 8.5F, 2F);
		builder.addVertex(1.075918F, 1.661816F, 0.00125F, 8.875F, 2F);
		builder.addVertex(1.075918F, 1.661816F, -0.18625F, 8.875F, 2.375F);

		builder.addVertex(1.075918F, 1.661816F, -0.18625F, 8.875F, 2.375F);
		builder.addVertex(1.075918F, 1.661816F, 0.00125F, 9.25F, 2.375F);
		builder.addVertex(0.537882F, 1.497732F, 0.00125F, 9.25F, 3.5F);
		builder.addVertex(0.537882F, 1.497732F, -0.18625F, 8.875F, 3.5F);

		builder.addVertex(1.021224F, 1.841161F, -0.18625F, 8.5F, 2.375F);
		builder.addVertex(0.483187F, 1.677078F, -0.18625F, 8.5F, 3.5F);
		builder.addVertex(0.483187F, 1.677078F, 0.00125F, 8.125F, 3.5F);
		builder.addVertex(1.021224F, 1.841161F, 0.00125F, 8.125F, 2.375F);

		//rope_left
		builder.addVertex(0.96875F, 1.71875F, 0.4375F, 10F, 6.375F);
		builder.addVertex(1.21875F, 1.71875F, 0.4375F, 10.5F, 6.375F);
		builder.addVertex(1.21875F, 1.46875F, 0.4375F, 10.5F, 6.875F);
		builder.addVertex(0.96875F, 1.46875F, 0.4375F, 10F, 6.875F);

		builder.addVertex(0.96875F, 1.71875F, 0.5625F, 11.25F, 6.375F);
		builder.addVertex(0.96875F, 1.46875F, 0.5625F, 11.25F, 6.875F);
		builder.addVertex(1.21875F, 1.46875F, 0.5625F, 10.75F, 6.875F);
		builder.addVertex(1.21875F, 1.71875F, 0.5625F, 10.75F, 6.375F);

		builder.addVertex(0.96875F, 1.46875F, 0.4375F, 10.5F, 6.375F);
		builder.addVertex(1.21875F, 1.46875F, 0.4375F, 11F, 6.375F);
		builder.addVertex(1.21875F, 1.46875F, 0.5625F, 11F, 6.125F);
		builder.addVertex(0.96875F, 1.46875F, 0.5625F, 10.5F, 6.125F);

		builder.addVertex(0.96875F, 1.71875F, 0.4375F, 10F, 6.375F);
		builder.addVertex(0.96875F, 1.71875F, 0.5625F, 10F, 6.125F);
		builder.addVertex(1.21875F, 1.71875F, 0.5625F, 10.5F, 6.125F);
		builder.addVertex(1.21875F, 1.71875F, 0.4375F, 10.5F, 6.375F);

		builder.addVertex(1.21875F, 1.71875F, 0.4375F, 10.5F, 6.375F);
		builder.addVertex(1.21875F, 1.71875F, 0.5625F, 10.75F, 6.375F);
		builder.addVertex(1.21875F, 1.46875F, 0.5625F, 10.75F, 6.875F);
		builder.addVertex(1.21875F, 1.46875F, 0.4375F, 10.5F, 6.875F);

		builder.addVertex(0.96875F, 1.71875F, 0.4375F, 10F, 6.375F);
		builder.addVertex(0.96875F, 1.46875F, 0.4375F, 10F, 6.875F);
		builder.addVertex(0.96875F, 1.46875F, 0.5625F, 9.75F, 6.875F);
		builder.addVertex(0.96875F, 1.71875F, 0.5625F, 9.75F, 6.375F);

		//standard_front_right1
		builder.addVertex(0.1875F, 1.8125F, 0.1875F, 8F, 9.625F);
		builder.addVertex(0.375F, 1.8125F, 0.1875F, 8.375F, 9.625F);
		builder.addVertex(0.375F, 1.25F, 0.1875F, 8.375F, 10.75F);
		builder.addVertex(0.1875F, 1.25F, 0.1875F, 8F, 10.75F);

		builder.addVertex(0.1875F, 1.8125F, 0.375F, 9.125F, 9.625F);
		builder.addVertex(0.1875F, 1.25F, 0.375F, 9.125F, 10.75F);
		builder.addVertex(0.375F, 1.25F, 0.375F, 8.75F, 10.75F);
		builder.addVertex(0.375F, 1.8125F, 0.375F, 8.75F, 9.625F);

		builder.addVertex(0.1875F, 1.25F, 0.1875F, 8.375F, 9.625F);
		builder.addVertex(0.375F, 1.25F, 0.1875F, 8.75F, 9.625F);
		builder.addVertex(0.375F, 1.25F, 0.375F, 8.75F, 9.25F);
		builder.addVertex(0.1875F, 1.25F, 0.375F, 8.375F, 9.25F);

		builder.addVertex(0.1875F, 1.8125F, 0.1875F, 8F, 9.625F);
		builder.addVertex(0.1875F, 1.8125F, 0.375F, 8F, 9.25F);
		builder.addVertex(0.375F, 1.8125F, 0.375F, 8.375F, 9.25F);
		builder.addVertex(0.375F, 1.8125F, 0.1875F, 8.375F, 9.625F);

		builder.addVertex(0.375F, 1.8125F, 0.1875F, 8.375F, 9.625F);
		builder.addVertex(0.375F, 1.8125F, 0.375F, 8.75F, 9.625F);
		builder.addVertex(0.375F, 1.25F, 0.375F, 8.75F, 10.75F);
		builder.addVertex(0.375F, 1.25F, 0.1875F, 8.375F, 10.75F);

		builder.addVertex(0.1875F, 1.8125F, 0.1875F, 8F, 9.625F);
		builder.addVertex(0.1875F, 1.25F, 0.1875F, 8F, 10.75F);
		builder.addVertex(0.1875F, 1.25F, 0.375F, 7.625F, 10.75F);
		builder.addVertex(0.1875F, 1.8125F, 0.375F, 7.625F, 9.625F);

		//support_back_left1d
		builder.addVertex(0.99875F, 1.841161F, 1.021224F, 11.75F, 2.375F);
		builder.addVertex(1.18625F, 1.841161F, 1.021224F, 12.125F, 2.375F);
		builder.addVertex(1.18625F, 1.677078F, 0.483187F, 12.125F, 3.5F);
		builder.addVertex(0.99875F, 1.677078F, 0.483187F, 11.75F, 3.5F);

		builder.addVertex(0.99875F, 1.661816F, 1.075918F, 12.875F, 2.375F);
		builder.addVertex(0.99875F, 1.497732F, 0.537882F, 12.875F, 3.5F);
		builder.addVertex(1.18625F, 1.497732F, 0.537882F, 12.5F, 3.5F);
		builder.addVertex(1.18625F, 1.661816F, 1.075918F, 12.5F, 2.375F);

		builder.addVertex(0.99875F, 1.677078F, 0.483187F, 12.125F, 2.375F);
		builder.addVertex(1.18625F, 1.677078F, 0.483187F, 12.5F, 2.375F);
		builder.addVertex(1.18625F, 1.497732F, 0.537882F, 12.5F, 2F);
		builder.addVertex(0.99875F, 1.497732F, 0.537882F, 12.125F, 2F);

		builder.addVertex(0.99875F, 1.841161F, 1.021224F, 11.75F, 2.375F);
		builder.addVertex(0.99875F, 1.661816F, 1.075918F, 11.75F, 2F);
		builder.addVertex(1.18625F, 1.661816F, 1.075918F, 12.125F, 2F);
		builder.addVertex(1.18625F, 1.841161F, 1.021224F, 12.125F, 2.375F);

		builder.addVertex(1.18625F, 1.841161F, 1.021224F, 12.125F, 2.375F);
		builder.addVertex(1.18625F, 1.661816F, 1.075918F, 12.5F, 2.375F);
		builder.addVertex(1.18625F, 1.497732F, 0.537882F, 12.5F, 3.5F);
		builder.addVertex(1.18625F, 1.677078F, 0.483187F, 12.125F, 3.5F);

		builder.addVertex(0.99875F, 1.841161F, 1.021224F, 11.75F, 2.375F);
		builder.addVertex(0.99875F, 1.677078F, 0.483187F, 11.75F, 3.5F);
		builder.addVertex(0.99875F, 1.497732F, 0.537882F, 11.375F, 3.5F);
		builder.addVertex(0.99875F, 1.661816F, 1.075918F, 11.375F, 2.375F);

		//support_front_left1b
		builder.addVertex(1.036816F, 1.924082F, -0.186875F, 8.5F, 1.5F);
		builder.addVertex(1.1875F, 1.8125F, -0.186875F, 8.875F, 1.5F);
		builder.addVertex(1.075918F, 1.661816F, -0.186875F, 8.875F, 1.875F);
		builder.addVertex(0.925234F, 1.773398F, -0.186875F, 8.5F, 1.875F);

		builder.addVertex(1.036816F, 1.924082F, 0.000625F, 9.625F, 1.5F);
		builder.addVertex(0.925234F, 1.773398F, 0.000625F, 9.625F, 1.875F);
		builder.addVertex(1.075918F, 1.661816F, 0.000625F, 9.25F, 1.875F);
		builder.addVertex(1.1875F, 1.8125F, 0.000625F, 9.25F, 1.5F);

		builder.addVertex(0.925234F, 1.773398F, -0.186875F, 8.875F, 1.5F);
		builder.addVertex(1.075918F, 1.661816F, -0.186875F, 9.25F, 1.5F);
		builder.addVertex(1.075918F, 1.661816F, 0.000625F, 9.25F, 1.125F);
		builder.addVertex(0.925234F, 1.773398F, 0.000625F, 8.875F, 1.125F);

		builder.addVertex(1.036816F, 1.924082F, -0.186875F, 8.5F, 1.5F);
		builder.addVertex(1.036816F, 1.924082F, 0.000625F, 8.5F, 1.125F);
		builder.addVertex(1.1875F, 1.8125F, 0.000625F, 8.875F, 1.125F);
		builder.addVertex(1.1875F, 1.8125F, -0.186875F, 8.875F, 1.5F);

		builder.addVertex(1.1875F, 1.8125F, -0.186875F, 8.875F, 1.5F);
		builder.addVertex(1.1875F, 1.8125F, 0.000625F, 9.25F, 1.5F);
		builder.addVertex(1.075918F, 1.661816F, 0.000625F, 9.25F, 1.875F);
		builder.addVertex(1.075918F, 1.661816F, -0.186875F, 8.875F, 1.875F);

		builder.addVertex(1.036816F, 1.924082F, -0.186875F, 8.5F, 1.5F);
		builder.addVertex(0.925234F, 1.773398F, -0.186875F, 8.5F, 1.875F);
		builder.addVertex(0.925234F, 1.773398F, 0.000625F, 8.125F, 1.875F);
		builder.addVertex(1.036816F, 1.924082F, 0.000625F, 8.125F, 1.5F);

		//support_back_left1b
		builder.addVertex(0.999375F, 1.924082F, 1.036816F, 11.75F, 1.5F);
		builder.addVertex(1.186875F, 1.924082F, 1.036816F, 12.125F, 1.5F);
		builder.addVertex(1.186875F, 1.773398F, 0.925234F, 12.125F, 1.875F);
		builder.addVertex(0.999375F, 1.773398F, 0.925234F, 11.75F, 1.875F);

		builder.addVertex(0.999375F, 1.8125F, 1.1875F, 12.875F, 1.5F);
		builder.addVertex(0.999375F, 1.661816F, 1.075918F, 12.875F, 1.875F);
		builder.addVertex(1.186875F, 1.661816F, 1.075918F, 12.5F, 1.875F);
		builder.addVertex(1.186875F, 1.8125F, 1.1875F, 12.5F, 1.5F);

		builder.addVertex(0.999375F, 1.773398F, 0.925234F, 12.125F, 1.5F);
		builder.addVertex(1.186875F, 1.773398F, 0.925234F, 12.5F, 1.5F);
		builder.addVertex(1.186875F, 1.661816F, 1.075918F, 12.5F, 1.125F);
		builder.addVertex(0.999375F, 1.661816F, 1.075918F, 12.125F, 1.125F);

		builder.addVertex(0.999375F, 1.924082F, 1.036816F, 11.75F, 1.5F);
		builder.addVertex(0.999375F, 1.8125F, 1.1875F, 11.75F, 1.125F);
		builder.addVertex(1.186875F, 1.8125F, 1.1875F, 12.125F, 1.125F);
		builder.addVertex(1.186875F, 1.924082F, 1.036816F, 12.125F, 1.5F);

		builder.addVertex(1.186875F, 1.924082F, 1.036816F, 12.125F, 1.5F);
		builder.addVertex(1.186875F, 1.8125F, 1.1875F, 12.5F, 1.5F);
		builder.addVertex(1.186875F, 1.661816F, 1.075918F, 12.5F, 1.875F);
		builder.addVertex(1.186875F, 1.773398F, 0.925234F, 12.125F, 1.875F);

		builder.addVertex(0.999375F, 1.924082F, 1.036816F, 11.75F, 1.5F);
		builder.addVertex(0.999375F, 1.773398F, 0.925234F, 11.75F, 1.875F);
		builder.addVertex(0.999375F, 1.661816F, 1.075918F, 11.375F, 1.875F);
		builder.addVertex(0.999375F, 1.8125F, 1.1875F, 11.375F, 1.5F);

		//support_front_right1c
		builder.addVertex(-0.186875F, 1.8125F, -0.1875F, 10.125F, 4F);
		builder.addVertex(0.000625F, 1.8125F, -0.1875F, 10.5F, 4F);
		builder.addVertex(0.000625F, 1.661816F, -0.075918F, 10.5F, 4.375F);
		builder.addVertex(-0.186875F, 1.661816F, -0.075918F, 10.125F, 4.375F);

		builder.addVertex(-0.186875F, 1.924082F, -0.036816F, 11.25F, 4F);
		builder.addVertex(-0.186875F, 1.773398F, 0.074766F, 11.25F, 4.375F);
		builder.addVertex(0.000625F, 1.773398F, 0.074766F, 10.875F, 4.375F);
		builder.addVertex(0.000625F, 1.924082F, -0.036816F, 10.875F, 4F);

		builder.addVertex(-0.186875F, 1.661816F, -0.075918F, 10.5F, 4F);
		builder.addVertex(0.000625F, 1.661816F, -0.075918F, 10.875F, 4F);
		builder.addVertex(0.000625F, 1.773398F, 0.074766F, 10.875F, 3.625F);
		builder.addVertex(-0.186875F, 1.773398F, 0.074766F, 10.5F, 3.625F);

		builder.addVertex(-0.186875F, 1.8125F, -0.1875F, 10.125F, 4F);
		builder.addVertex(-0.186875F, 1.924082F, -0.036816F, 10.125F, 3.625F);
		builder.addVertex(0.000625F, 1.924082F, -0.036816F, 10.5F, 3.625F);
		builder.addVertex(0.000625F, 1.8125F, -0.1875F, 10.5F, 4F);

		builder.addVertex(0.000625F, 1.8125F, -0.1875F, 10.5F, 4F);
		builder.addVertex(0.000625F, 1.924082F, -0.036816F, 10.875F, 4F);
		builder.addVertex(0.000625F, 1.773398F, 0.074766F, 10.875F, 4.375F);
		builder.addVertex(0.000625F, 1.661816F, -0.075918F, 10.5F, 4.375F);

		builder.addVertex(-0.186875F, 1.8125F, -0.1875F, 10.125F, 4F);
		builder.addVertex(-0.186875F, 1.661816F, -0.075918F, 10.125F, 4.375F);
		builder.addVertex(-0.186875F, 1.773398F, 0.074766F, 9.75F, 4.375F);
		builder.addVertex(-0.186875F, 1.924082F, -0.036816F, 9.75F, 4F);

		//rope_front
		builder.addVertex(0.4375F, 1.71875F, -0.21875F, 8.625F, 6.625F);
		builder.addVertex(0.5625F, 1.71875F, -0.21875F, 8.875F, 6.625F);
		builder.addVertex(0.5625F, 1.46875F, -0.21875F, 8.875F, 7.125F);
		builder.addVertex(0.4375F, 1.46875F, -0.21875F, 8.625F, 7.125F);

		builder.addVertex(0.4375F, 1.71875F, 0.03125F, 9.625F, 6.625F);
		builder.addVertex(0.4375F, 1.46875F, 0.03125F, 9.625F, 7.125F);
		builder.addVertex(0.5625F, 1.46875F, 0.03125F, 9.375F, 7.125F);
		builder.addVertex(0.5625F, 1.71875F, 0.03125F, 9.375F, 6.625F);

		builder.addVertex(0.4375F, 1.46875F, -0.21875F, 8.875F, 6.625F);
		builder.addVertex(0.5625F, 1.46875F, -0.21875F, 9.125F, 6.625F);
		builder.addVertex(0.5625F, 1.46875F, 0.03125F, 9.125F, 6.125F);
		builder.addVertex(0.4375F, 1.46875F, 0.03125F, 8.875F, 6.125F);

		builder.addVertex(0.4375F, 1.71875F, -0.21875F, 8.625F, 6.625F);
		builder.addVertex(0.4375F, 1.71875F, 0.03125F, 8.625F, 6.125F);
		builder.addVertex(0.5625F, 1.71875F, 0.03125F, 8.875F, 6.125F);
		builder.addVertex(0.5625F, 1.71875F, -0.21875F, 8.875F, 6.625F);

		builder.addVertex(0.5625F, 1.71875F, -0.21875F, 8.875F, 6.625F);
		builder.addVertex(0.5625F, 1.71875F, 0.03125F, 9.375F, 6.625F);
		builder.addVertex(0.5625F, 1.46875F, 0.03125F, 9.375F, 7.125F);
		builder.addVertex(0.5625F, 1.46875F, -0.21875F, 8.875F, 7.125F);

		builder.addVertex(0.4375F, 1.71875F, -0.21875F, 8.625F, 6.625F);
		builder.addVertex(0.4375F, 1.46875F, -0.21875F, 8.625F, 7.125F);
		builder.addVertex(0.4375F, 1.46875F, 0.03125F, 8.125F, 7.125F);
		builder.addVertex(0.4375F, 1.71875F, 0.03125F, 8.125F, 6.625F);

		//standard_back_left1
		builder.addVertex(0.625F, 1.8125F, 0.625F, 9.625F, 9.625F);
		builder.addVertex(0.8125F, 1.8125F, 0.625F, 10F, 9.625F);
		builder.addVertex(0.8125F, 1.25F, 0.625F, 10F, 10.75F);
		builder.addVertex(0.625F, 1.25F, 0.625F, 9.625F, 10.75F);

		builder.addVertex(0.625F, 1.8125F, 0.8125F, 10.75F, 9.625F);
		builder.addVertex(0.625F, 1.25F, 0.8125F, 10.75F, 10.75F);
		builder.addVertex(0.8125F, 1.25F, 0.8125F, 10.375F, 10.75F);
		builder.addVertex(0.8125F, 1.8125F, 0.8125F, 10.375F, 9.625F);

		builder.addVertex(0.625F, 1.25F, 0.625F, 10F, 9.625F);
		builder.addVertex(0.8125F, 1.25F, 0.625F, 10.375F, 9.625F);
		builder.addVertex(0.8125F, 1.25F, 0.8125F, 10.375F, 9.25F);
		builder.addVertex(0.625F, 1.25F, 0.8125F, 10F, 9.25F);

		builder.addVertex(0.625F, 1.8125F, 0.625F, 9.625F, 9.625F);
		builder.addVertex(0.625F, 1.8125F, 0.8125F, 9.625F, 9.25F);
		builder.addVertex(0.8125F, 1.8125F, 0.8125F, 10F, 9.25F);
		builder.addVertex(0.8125F, 1.8125F, 0.625F, 10F, 9.625F);

		builder.addVertex(0.8125F, 1.8125F, 0.625F, 10F, 9.625F);
		builder.addVertex(0.8125F, 1.8125F, 0.8125F, 10.375F, 9.625F);
		builder.addVertex(0.8125F, 1.25F, 0.8125F, 10.375F, 10.75F);
		builder.addVertex(0.8125F, 1.25F, 0.625F, 10F, 10.75F);

		builder.addVertex(0.625F, 1.8125F, 0.625F, 9.625F, 9.625F);
		builder.addVertex(0.625F, 1.25F, 0.625F, 9.625F, 10.75F);
		builder.addVertex(0.625F, 1.25F, 0.8125F, 9.25F, 10.75F);
		builder.addVertex(0.625F, 1.8125F, 0.8125F, 9.25F, 9.625F);

		//support_back_right1a
		builder.addVertex(-0.1875F, 2.125F, 1F, 13.375F, 0.375F);
		builder.addVertex(0F, 2.125F, 1F, 13.75F, 0.375F);
		builder.addVertex(0F, 1.8125F, 1F, 13.75F, 1F);
		builder.addVertex(-0.1875F, 1.8125F, 1F, 13.375F, 1F);

		builder.addVertex(-0.1875F, 2.125F, 1.1875F, 14.5F, 0.375F);
		builder.addVertex(-0.1875F, 1.8125F, 1.1875F, 14.5F, 1F);
		builder.addVertex(0F, 1.8125F, 1.1875F, 14.125F, 1F);
		builder.addVertex(0F, 2.125F, 1.1875F, 14.125F, 0.375F);

		builder.addVertex(-0.1875F, 1.8125F, 1F, 13.75F, 0.375F);
		builder.addVertex(0F, 1.8125F, 1F, 14.125F, 0.375F);
		builder.addVertex(0F, 1.8125F, 1.1875F, 14.125F, 0F);
		builder.addVertex(-0.1875F, 1.8125F, 1.1875F, 13.75F, 0F);

		builder.addVertex(-0.1875F, 2.125F, 1F, 13.375F, 0.375F);
		builder.addVertex(-0.1875F, 2.125F, 1.1875F, 13.375F, 0F);
		builder.addVertex(0F, 2.125F, 1.1875F, 13.75F, 0F);
		builder.addVertex(0F, 2.125F, 1F, 13.75F, 0.375F);

		builder.addVertex(0F, 2.125F, 1F, 13.75F, 0.375F);
		builder.addVertex(0F, 2.125F, 1.1875F, 14.125F, 0.375F);
		builder.addVertex(0F, 1.8125F, 1.1875F, 14.125F, 1F);
		builder.addVertex(0F, 1.8125F, 1F, 13.75F, 1F);

		builder.addVertex(-0.1875F, 2.125F, 1F, 13.375F, 0.375F);
		builder.addVertex(-0.1875F, 1.8125F, 1F, 13.375F, 1F);
		builder.addVertex(-0.1875F, 1.8125F, 1.1875F, 13F, 1F);
		builder.addVertex(-0.1875F, 2.125F, 1.1875F, 13F, 0.375F);

		//standardrope_back_left1
		builder.addVertex(0.59056F, 1.318019F, 0.590207F, 6.5F, 15.125F);
		builder.addVertex(0.840298F, 1.329451F, 0.590589F, 7F, 15.125F);
		builder.addVertex(0.845999F, 1.204711F, 0.596284F, 7F, 15.375F);
		builder.addVertex(0.596261F, 1.193279F, 0.595902F, 6.5F, 15.375F);

		builder.addVertex(0.589658F, 1.32938F, 0.839947F, 8F, 15.125F);
		builder.addVertex(0.595359F, 1.204641F, 0.845642F, 8F, 15.375F);
		builder.addVertex(0.845097F, 1.216072F, 0.846024F, 7.5F, 15.375F);
		builder.addVertex(0.839396F, 1.340812F, 0.840329F, 7.5F, 15.125F);

		builder.addVertex(0.596261F, 1.193279F, 0.595902F, 7F, 15.125F);
		builder.addVertex(0.845999F, 1.204711F, 0.596284F, 7.5F, 15.125F);
		builder.addVertex(0.845097F, 1.216072F, 0.846024F, 7.5F, 14.625F);
		builder.addVertex(0.595359F, 1.204641F, 0.845642F, 7F, 14.625F);

		builder.addVertex(0.59056F, 1.318019F, 0.590207F, 6.5F, 15.125F);
		builder.addVertex(0.589658F, 1.32938F, 0.839947F, 6.5F, 14.625F);
		builder.addVertex(0.839396F, 1.340812F, 0.840329F, 7F, 14.625F);
		builder.addVertex(0.840298F, 1.329451F, 0.590589F, 7F, 15.125F);

		builder.addVertex(0.840298F, 1.329451F, 0.590589F, 7F, 15.125F);
		builder.addVertex(0.839396F, 1.340812F, 0.840329F, 7.5F, 15.125F);
		builder.addVertex(0.845097F, 1.216072F, 0.846024F, 7.5F, 15.375F);
		builder.addVertex(0.845999F, 1.204711F, 0.596284F, 7F, 15.375F);

		builder.addVertex(0.59056F, 1.318019F, 0.590207F, 6.5F, 15.125F);
		builder.addVertex(0.596261F, 1.193279F, 0.595902F, 6.5F, 15.375F);
		builder.addVertex(0.595359F, 1.204641F, 0.845642F, 6F, 15.375F);
		builder.addVertex(0.589658F, 1.32938F, 0.839947F, 6F, 15.125F);

		//support_back_left1c
		builder.addVertex(1.036816F, 1.924082F, 0.999375F, 11.75F, 4F);
		builder.addVertex(1.1875F, 1.8125F, 0.999375F, 12.125F, 4F);
		builder.addVertex(1.075918F, 1.661816F, 0.999375F, 12.125F, 4.375F);
		builder.addVertex(0.925234F, 1.773398F, 0.999375F, 11.75F, 4.375F);

		builder.addVertex(1.036816F, 1.924082F, 1.186875F, 12.875F, 4F);
		builder.addVertex(0.925234F, 1.773398F, 1.186875F, 12.875F, 4.375F);
		builder.addVertex(1.075918F, 1.661816F, 1.186875F, 12.5F, 4.375F);
		builder.addVertex(1.1875F, 1.8125F, 1.186875F, 12.5F, 4F);

		builder.addVertex(0.925234F, 1.773398F, 0.999375F, 12.125F, 4F);
		builder.addVertex(1.075918F, 1.661816F, 0.999375F, 12.5F, 4F);
		builder.addVertex(1.075918F, 1.661816F, 1.186875F, 12.5F, 3.625F);
		builder.addVertex(0.925234F, 1.773398F, 1.186875F, 12.125F, 3.625F);

		builder.addVertex(1.036816F, 1.924082F, 0.999375F, 11.75F, 4F);
		builder.addVertex(1.036816F, 1.924082F, 1.186875F, 11.75F, 3.625F);
		builder.addVertex(1.1875F, 1.8125F, 1.186875F, 12.125F, 3.625F);
		builder.addVertex(1.1875F, 1.8125F, 0.999375F, 12.125F, 4F);

		builder.addVertex(1.1875F, 1.8125F, 0.999375F, 12.125F, 4F);
		builder.addVertex(1.1875F, 1.8125F, 1.186875F, 12.5F, 4F);
		builder.addVertex(1.075918F, 1.661816F, 1.186875F, 12.5F, 4.375F);
		builder.addVertex(1.075918F, 1.661816F, 0.999375F, 12.125F, 4.375F);

		builder.addVertex(1.036816F, 1.924082F, 0.999375F, 11.75F, 4F);
		builder.addVertex(0.925234F, 1.773398F, 0.999375F, 11.75F, 4.375F);
		builder.addVertex(0.925234F, 1.773398F, 1.186875F, 11.375F, 4.375F);
		builder.addVertex(1.036816F, 1.924082F, 1.186875F, 11.375F, 4F);

		//support_front_right1a
		builder.addVertex(-0.1875F, 2.125F, -0.1875F, 10.125F, 0.375F);
		builder.addVertex(-0F, 2.125F, -0.1875F, 10.5F, 0.375F);
		builder.addVertex(-0F, 1.8125F, -0.1875F, 10.5F, 1F);
		builder.addVertex(-0.1875F, 1.8125F, -0.1875F, 10.125F, 1F);

		builder.addVertex(-0.1875F, 2.125F, 0F, 11.25F, 0.375F);
		builder.addVertex(-0.1875F, 1.8125F, 0F, 11.25F, 1F);
		builder.addVertex(-0F, 1.8125F, 0F, 10.875F, 1F);
		builder.addVertex(0F, 2.125F, -0F, 10.875F, 0.375F);

		builder.addVertex(-0.1875F, 1.8125F, -0.1875F, 10.5F, 0.375F);
		builder.addVertex(-0F, 1.8125F, -0.1875F, 10.875F, 0.375F);
		builder.addVertex(-0F, 1.8125F, 0F, 10.875F, 0F);
		builder.addVertex(-0.1875F, 1.8125F, 0F, 10.5F, 0F);

		builder.addVertex(-0.1875F, 2.125F, -0.1875F, 10.125F, 0.375F);
		builder.addVertex(-0.1875F, 2.125F, 0F, 10.125F, 0F);
		builder.addVertex(0F, 2.125F, -0F, 10.5F, 0F);
		builder.addVertex(-0F, 2.125F, -0.1875F, 10.5F, 0.375F);

		builder.addVertex(-0F, 2.125F, -0.1875F, 10.5F, 0.375F);
		builder.addVertex(0F, 2.125F, -0F, 10.875F, 0.375F);
		builder.addVertex(-0F, 1.8125F, 0F, 10.875F, 1F);
		builder.addVertex(-0F, 1.8125F, -0.1875F, 10.5F, 1F);

		builder.addVertex(-0.1875F, 2.125F, -0.1875F, 10.125F, 0.375F);
		builder.addVertex(-0.1875F, 1.8125F, -0.1875F, 10.125F, 1F);
		builder.addVertex(-0.1875F, 1.8125F, 0F, 9.75F, 1F);
		builder.addVertex(-0.1875F, 2.125F, 0F, 9.75F, 0.375F);

		//standardrope_front_right1
		builder.addVertex(0.160604F, 1.340812F, 0.159671F, 6.5F, 15.125F);
		builder.addVertex(0.410342F, 1.32938F, 0.160053F, 7F, 15.125F);
		builder.addVertex(0.404641F, 1.20464F, 0.154358F, 7F, 15.375F);
		builder.addVertex(0.154903F, 1.216072F, 0.153976F, 6.5F, 15.375F);

		builder.addVertex(0.159702F, 1.329451F, 0.409411F, 8F, 15.125F);
		builder.addVertex(0.154001F, 1.204711F, 0.403716F, 8F, 15.375F);
		builder.addVertex(0.403739F, 1.193279F, 0.404098F, 7.5F, 15.375F);
		builder.addVertex(0.40944F, 1.318019F, 0.409793F, 7.5F, 15.125F);

		builder.addVertex(0.154903F, 1.216072F, 0.153976F, 7F, 15.125F);
		builder.addVertex(0.404641F, 1.20464F, 0.154358F, 7.5F, 15.125F);
		builder.addVertex(0.403739F, 1.193279F, 0.404098F, 7.5F, 14.625F);
		builder.addVertex(0.154001F, 1.204711F, 0.403716F, 7F, 14.625F);

		builder.addVertex(0.160604F, 1.340812F, 0.159671F, 6.5F, 15.125F);
		builder.addVertex(0.159702F, 1.329451F, 0.409411F, 6.5F, 14.625F);
		builder.addVertex(0.40944F, 1.318019F, 0.409793F, 7F, 14.625F);
		builder.addVertex(0.410342F, 1.32938F, 0.160053F, 7F, 15.125F);

		builder.addVertex(0.410342F, 1.32938F, 0.160053F, 7F, 15.125F);
		builder.addVertex(0.40944F, 1.318019F, 0.409793F, 7.5F, 15.125F);
		builder.addVertex(0.403739F, 1.193279F, 0.404098F, 7.5F, 15.375F);
		builder.addVertex(0.404641F, 1.20464F, 0.154358F, 7F, 15.375F);

		builder.addVertex(0.160604F, 1.340812F, 0.159671F, 6.5F, 15.125F);
		builder.addVertex(0.154903F, 1.216072F, 0.153976F, 6.5F, 15.375F);
		builder.addVertex(0.154001F, 1.204711F, 0.403716F, 6F, 15.375F);
		builder.addVertex(0.159702F, 1.329451F, 0.409411F, 6F, 15.125F);

		//standard_front_right2
		builder.addVertex(0.189343F, 1.284091F, 0.188769F, 8F, 11.25F);
		builder.addVertex(0.376065F, 1.267032F, 0.188278F, 8.375F, 11.25F);
		builder.addVertex(0.313612F, 0.585238F, 0.12573F, 8.375F, 12.625F);
		builder.addVertex(0.126891F, 0.602297F, 0.126221F, 8F, 12.625F);

		builder.addVertex(0.188278F, 1.267059F, 0.375491F, 9.125F, 11.25F);
		builder.addVertex(0.125825F, 0.585264F, 0.312942F, 9.125F, 12.625F);
		builder.addVertex(0.312547F, 0.568206F, 0.312452F, 8.75F, 12.625F);
		builder.addVertex(0.375F, 1.25F, 0.375F, 8.75F, 11.25F);

		builder.addVertex(0.126891F, 0.602297F, 0.126221F, 8.375F, 11.25F);
		builder.addVertex(0.313612F, 0.585238F, 0.12573F, 8.75F, 11.25F);
		builder.addVertex(0.312547F, 0.568206F, 0.312452F, 8.75F, 10.875F);
		builder.addVertex(0.125825F, 0.585264F, 0.312942F, 8.375F, 10.875F);

		builder.addVertex(0.189343F, 1.284091F, 0.188769F, 8F, 11.25F);
		builder.addVertex(0.188278F, 1.267059F, 0.375491F, 8F, 10.875F);
		builder.addVertex(0.375F, 1.25F, 0.375F, 8.375F, 10.875F);
		builder.addVertex(0.376065F, 1.267032F, 0.188278F, 8.375F, 11.25F);

		builder.addVertex(0.376065F, 1.267032F, 0.188278F, 8.375F, 11.25F);
		builder.addVertex(0.375F, 1.25F, 0.375F, 8.75F, 11.25F);
		builder.addVertex(0.312547F, 0.568206F, 0.312452F, 8.75F, 12.625F);
		builder.addVertex(0.313612F, 0.585238F, 0.12573F, 8.375F, 12.625F);

		builder.addVertex(0.189343F, 1.284091F, 0.188769F, 8F, 11.25F);
		builder.addVertex(0.126891F, 0.602297F, 0.126221F, 8F, 12.625F);
		builder.addVertex(0.125825F, 0.585264F, 0.312942F, 7.625F, 12.625F);
		builder.addVertex(0.188278F, 1.267059F, 0.375491F, 7.625F, 11.25F);

		//support_back_right1b
		builder.addVertex(-0.186875F, 1.924082F, 1.036816F, 13.375F, 1.5F);
		builder.addVertex(0.000625F, 1.924082F, 1.036816F, 13.75F, 1.5F);
		builder.addVertex(0.000625F, 1.773398F, 0.925234F, 13.75F, 1.875F);
		builder.addVertex(-0.186875F, 1.773398F, 0.925234F, 13.375F, 1.875F);

		builder.addVertex(-0.186875F, 1.8125F, 1.1875F, 14.5F, 1.5F);
		builder.addVertex(-0.186875F, 1.661816F, 1.075918F, 14.5F, 1.875F);
		builder.addVertex(0.000625F, 1.661816F, 1.075918F, 14.125F, 1.875F);
		builder.addVertex(0.000625F, 1.8125F, 1.1875F, 14.125F, 1.5F);

		builder.addVertex(-0.186875F, 1.773398F, 0.925234F, 13.75F, 1.5F);
		builder.addVertex(0.000625F, 1.773398F, 0.925234F, 14.125F, 1.5F);
		builder.addVertex(0.000625F, 1.661816F, 1.075918F, 14.125F, 1.125F);
		builder.addVertex(-0.186875F, 1.661816F, 1.075918F, 13.75F, 1.125F);

		builder.addVertex(-0.186875F, 1.924082F, 1.036816F, 13.375F, 1.5F);
		builder.addVertex(-0.186875F, 1.8125F, 1.1875F, 13.375F, 1.125F);
		builder.addVertex(0.000625F, 1.8125F, 1.1875F, 13.75F, 1.125F);
		builder.addVertex(0.000625F, 1.924082F, 1.036816F, 13.75F, 1.5F);

		builder.addVertex(0.000625F, 1.924082F, 1.036816F, 13.75F, 1.5F);
		builder.addVertex(0.000625F, 1.8125F, 1.1875F, 14.125F, 1.5F);
		builder.addVertex(0.000625F, 1.661816F, 1.075918F, 14.125F, 1.875F);
		builder.addVertex(0.000625F, 1.773398F, 0.925234F, 13.75F, 1.875F);

		builder.addVertex(-0.186875F, 1.924082F, 1.036816F, 13.375F, 1.5F);
		builder.addVertex(-0.186875F, 1.773398F, 0.925234F, 13.375F, 1.875F);
		builder.addVertex(-0.186875F, 1.661816F, 1.075918F, 13F, 1.875F);
		builder.addVertex(-0.186875F, 1.8125F, 1.1875F, 13F, 1.5F);

		//standard_front_left1
		builder.addVertex(0.625F, 1.8125F, 0.1875F, 6.375F, 9.625F);
		builder.addVertex(0.8125F, 1.8125F, 0.1875F, 6.75F, 9.625F);
		builder.addVertex(0.8125F, 1.25F, 0.1875F, 6.75F, 10.75F);
		builder.addVertex(0.625F, 1.25F, 0.1875F, 6.375F, 10.75F);

		builder.addVertex(0.625F, 1.8125F, 0.375F, 7.5F, 9.625F);
		builder.addVertex(0.625F, 1.25F, 0.375F, 7.5F, 10.75F);
		builder.addVertex(0.8125F, 1.25F, 0.375F, 7.125F, 10.75F);
		builder.addVertex(0.8125F, 1.8125F, 0.375F, 7.125F, 9.625F);

		builder.addVertex(0.625F, 1.25F, 0.1875F, 6.75F, 9.625F);
		builder.addVertex(0.8125F, 1.25F, 0.1875F, 7.125F, 9.625F);
		builder.addVertex(0.8125F, 1.25F, 0.375F, 7.125F, 9.25F);
		builder.addVertex(0.625F, 1.25F, 0.375F, 6.75F, 9.25F);

		builder.addVertex(0.625F, 1.8125F, 0.1875F, 6.375F, 9.625F);
		builder.addVertex(0.625F, 1.8125F, 0.375F, 6.375F, 9.25F);
		builder.addVertex(0.8125F, 1.8125F, 0.375F, 6.75F, 9.25F);
		builder.addVertex(0.8125F, 1.8125F, 0.1875F, 6.75F, 9.625F);

		builder.addVertex(0.8125F, 1.8125F, 0.1875F, 6.75F, 9.625F);
		builder.addVertex(0.8125F, 1.8125F, 0.375F, 7.125F, 9.625F);
		builder.addVertex(0.8125F, 1.25F, 0.375F, 7.125F, 10.75F);
		builder.addVertex(0.8125F, 1.25F, 0.1875F, 6.75F, 10.75F);

		builder.addVertex(0.625F, 1.8125F, 0.1875F, 6.375F, 9.625F);
		builder.addVertex(0.625F, 1.25F, 0.1875F, 6.375F, 10.75F);
		builder.addVertex(0.625F, 1.25F, 0.375F, 6F, 10.75F);
		builder.addVertex(0.625F, 1.8125F, 0.375F, 6F, 9.625F);

		//connection_back
		builder.addVertex(0.40625F, 1.52515F, 0.732982F, 6.625F, 5.125F);
		builder.addVertex(0.59375F, 1.52515F, 0.732982F, 7F, 5.125F);
		builder.addVertex(0.59375F, 1.364939F, 0.830391F, 7F, 5.5F);
		builder.addVertex(0.40625F, 1.364939F, 0.830392F, 6.625F, 5.5F);

		builder.addVertex(0.40625F, 1.6875F, 1F, 8F, 5.125F);
		builder.addVertex(0.40625F, 1.527289F, 1.09741F, 8F, 5.5F);
		builder.addVertex(0.59375F, 1.527289F, 1.09741F, 7.625F, 5.5F);
		builder.addVertex(0.59375F, 1.6875F, 1F, 7.625F, 5.125F);

		builder.addVertex(0.40625F, 1.364939F, 0.830392F, 7F, 5.125F);
		builder.addVertex(0.59375F, 1.364939F, 0.830391F, 7.375F, 5.125F);
		builder.addVertex(0.59375F, 1.527289F, 1.09741F, 7.375F, 4.5F);
		builder.addVertex(0.40625F, 1.527289F, 1.09741F, 7F, 4.5F);

		builder.addVertex(0.40625F, 1.52515F, 0.732982F, 6.625F, 5.125F);
		builder.addVertex(0.40625F, 1.6875F, 1F, 6.625F, 4.5F);
		builder.addVertex(0.59375F, 1.6875F, 1F, 7F, 4.5F);
		builder.addVertex(0.59375F, 1.52515F, 0.732982F, 7F, 5.125F);

		builder.addVertex(0.59375F, 1.52515F, 0.732982F, 7F, 5.125F);
		builder.addVertex(0.59375F, 1.6875F, 1F, 7.625F, 5.125F);
		builder.addVertex(0.59375F, 1.527289F, 1.09741F, 7.625F, 5.5F);
		builder.addVertex(0.59375F, 1.364939F, 0.830391F, 7F, 5.5F);

		builder.addVertex(0.40625F, 1.52515F, 0.732982F, 6.625F, 5.125F);
		builder.addVertex(0.40625F, 1.364939F, 0.830392F, 6.625F, 5.5F);
		builder.addVertex(0.40625F, 1.527289F, 1.09741F, 6F, 5.5F);
		builder.addVertex(0.40625F, 1.6875F, 1F, 6F, 5.125F);

		//standardrope_front_left2
		builder.addVertex(0.643825F, 0.663238F, 0.105526F, 6.5F, 15.125F);
		builder.addVertex(0.891486F, 0.69733F, 0.10684F, 7F, 15.125F);
		builder.addVertex(0.908462F, 0.574661F, 0.089834F, 7F, 15.375F);
		builder.addVertex(0.660801F, 0.540569F, 0.088521F, 6.5F, 15.375F);

		builder.addVertex(0.647174F, 0.629366F, 0.353199F, 8F, 15.125F);
		builder.addVertex(0.66415F, 0.506698F, 0.336193F, 8F, 15.375F);
		builder.addVertex(0.911811F, 0.54079F, 0.337506F, 7.5F, 15.375F);
		builder.addVertex(0.894835F, 0.663459F, 0.354512F, 7.5F, 15.125F);

		builder.addVertex(0.660801F, 0.540569F, 0.088521F, 7F, 15.125F);
		builder.addVertex(0.908462F, 0.574661F, 0.089834F, 7.5F, 15.125F);
		builder.addVertex(0.911811F, 0.54079F, 0.337506F, 7.5F, 14.625F);
		builder.addVertex(0.66415F, 0.506698F, 0.336193F, 7F, 14.625F);

		builder.addVertex(0.643825F, 0.663238F, 0.105526F, 6.5F, 15.125F);
		builder.addVertex(0.647174F, 0.629366F, 0.353199F, 6.5F, 14.625F);
		builder.addVertex(0.894835F, 0.663459F, 0.354512F, 7F, 14.625F);
		builder.addVertex(0.891486F, 0.69733F, 0.10684F, 7F, 15.125F);

		builder.addVertex(0.891486F, 0.69733F, 0.10684F, 7F, 15.125F);
		builder.addVertex(0.894835F, 0.663459F, 0.354512F, 7.5F, 15.125F);
		builder.addVertex(0.911811F, 0.54079F, 0.337506F, 7.5F, 15.375F);
		builder.addVertex(0.908462F, 0.574661F, 0.089834F, 7F, 15.375F);

		builder.addVertex(0.643825F, 0.663238F, 0.105526F, 6.5F, 15.125F);
		builder.addVertex(0.660801F, 0.540569F, 0.088521F, 6.5F, 15.375F);
		builder.addVertex(0.66415F, 0.506698F, 0.336193F, 6F, 15.375F);
		builder.addVertex(0.647174F, 0.629366F, 0.353199F, 6F, 15.125F);

		//brazier_edge_front2
		builder.addVertex(-0F, 2.104065F, -0.247203F, 0.375F, 3.75F);
		builder.addVertex(1F, 2.104065F, -0.247203F, 2.375F, 3.75F);
		builder.addVertex(1F, 1.943854F, -0.344613F, 2.375F, 4.125F);
		builder.addVertex(-0F, 1.943854F, -0.344613F, 0.375F, 4.125F);

		builder.addVertex(-0F, 2.006655F, -0.086992F, 4.75F, 3.75F);
		builder.addVertex(-0F, 1.846444F, -0.184402F, 4.75F, 4.125F);
		builder.addVertex(1F, 1.846444F, -0.184402F, 2.75F, 4.125F);
		builder.addVertex(1F, 2.006655F, -0.086992F, 2.75F, 3.75F);

		builder.addVertex(-0F, 1.943854F, -0.344613F, 2.375F, 3.75F);
		builder.addVertex(1F, 1.943854F, -0.344613F, 4.375F, 3.75F);
		builder.addVertex(1F, 1.846444F, -0.184402F, 4.375F, 3.375F);
		builder.addVertex(-0F, 1.846444F, -0.184402F, 2.375F, 3.375F);

		builder.addVertex(-0F, 2.104065F, -0.247203F, 0.375F, 3.75F);
		builder.addVertex(-0F, 2.006655F, -0.086992F, 0.375F, 3.375F);
		builder.addVertex(1F, 2.006655F, -0.086992F, 2.375F, 3.375F);
		builder.addVertex(1F, 2.104065F, -0.247203F, 2.375F, 3.75F);

		builder.addVertex(1F, 2.104065F, -0.247203F, 2.375F, 3.75F);
		builder.addVertex(1F, 2.006655F, -0.086992F, 2.75F, 3.75F);
		builder.addVertex(1F, 1.846444F, -0.184402F, 2.75F, 4.125F);
		builder.addVertex(1F, 1.943854F, -0.344613F, 2.375F, 4.125F);

		builder.addVertex(-0F, 2.104065F, -0.247203F, 0.375F, 3.75F);
		builder.addVertex(-0F, 1.943854F, -0.344613F, 0.375F, 4.125F);
		builder.addVertex(-0F, 1.846444F, -0.184402F, 0F, 4.125F);
		builder.addVertex(-0F, 2.006655F, -0.086992F, 0F, 3.75F);

		//standardrope_front_right2
		builder.addVertex(0.108514F, 0.69733F, 0.10684F, 6.5F, 15.125F);
		builder.addVertex(0.356175F, 0.663238F, 0.105526F, 7F, 15.125F);
		builder.addVertex(0.339198F, 0.540569F, 0.088521F, 7F, 15.375F);
		builder.addVertex(0.091537F, 0.574661F, 0.089834F, 6.5F, 15.375F);

		builder.addVertex(0.105165F, 0.663459F, 0.354512F, 8F, 15.125F);
		builder.addVertex(0.088188F, 0.54079F, 0.337507F, 8F, 15.375F);
		builder.addVertex(0.335849F, 0.506698F, 0.336193F, 7.5F, 15.375F);
		builder.addVertex(0.352826F, 0.629366F, 0.353199F, 7.5F, 15.125F);

		builder.addVertex(0.091537F, 0.574661F, 0.089834F, 7F, 15.125F);
		builder.addVertex(0.339198F, 0.540569F, 0.088521F, 7.5F, 15.125F);
		builder.addVertex(0.335849F, 0.506698F, 0.336193F, 7.5F, 14.625F);
		builder.addVertex(0.088188F, 0.54079F, 0.337507F, 7F, 14.625F);

		builder.addVertex(0.108514F, 0.69733F, 0.10684F, 6.5F, 15.125F);
		builder.addVertex(0.105165F, 0.663459F, 0.354512F, 6.5F, 14.625F);
		builder.addVertex(0.352826F, 0.629366F, 0.353199F, 7F, 14.625F);
		builder.addVertex(0.356175F, 0.663238F, 0.105526F, 7F, 15.125F);

		builder.addVertex(0.356175F, 0.663238F, 0.105526F, 7F, 15.125F);
		builder.addVertex(0.352826F, 0.629366F, 0.353199F, 7.5F, 15.125F);
		builder.addVertex(0.335849F, 0.506698F, 0.336193F, 7.5F, 15.375F);
		builder.addVertex(0.339198F, 0.540569F, 0.088521F, 7F, 15.375F);

		builder.addVertex(0.108514F, 0.69733F, 0.10684F, 6.5F, 15.125F);
		builder.addVertex(0.091537F, 0.574661F, 0.089834F, 6.5F, 15.375F);
		builder.addVertex(0.088188F, 0.54079F, 0.337507F, 6F, 15.375F);
		builder.addVertex(0.105165F, 0.663459F, 0.354512F, 6F, 15.125F);

		//rope_right
		builder.addVertex(-0.21875F, 1.71875F, 0.4375F, 11.625F, 6.375F);
		builder.addVertex(0.03125F, 1.71875F, 0.4375F, 12.125F, 6.375F);
		builder.addVertex(0.03125F, 1.46875F, 0.4375F, 12.125F, 6.875F);
		builder.addVertex(-0.21875F, 1.46875F, 0.4375F, 11.625F, 6.875F);

		builder.addVertex(-0.21875F, 1.71875F, 0.5625F, 12.875F, 6.375F);
		builder.addVertex(-0.21875F, 1.46875F, 0.5625F, 12.875F, 6.875F);
		builder.addVertex(0.03125F, 1.46875F, 0.5625F, 12.375F, 6.875F);
		builder.addVertex(0.03125F, 1.71875F, 0.5625F, 12.375F, 6.375F);

		builder.addVertex(-0.21875F, 1.46875F, 0.4375F, 12.125F, 6.375F);
		builder.addVertex(0.03125F, 1.46875F, 0.4375F, 12.625F, 6.375F);
		builder.addVertex(0.03125F, 1.46875F, 0.5625F, 12.625F, 6.125F);
		builder.addVertex(-0.21875F, 1.46875F, 0.5625F, 12.125F, 6.125F);

		builder.addVertex(-0.21875F, 1.71875F, 0.4375F, 11.625F, 6.375F);
		builder.addVertex(-0.21875F, 1.71875F, 0.5625F, 11.625F, 6.125F);
		builder.addVertex(0.03125F, 1.71875F, 0.5625F, 12.125F, 6.125F);
		builder.addVertex(0.03125F, 1.71875F, 0.4375F, 12.125F, 6.375F);

		builder.addVertex(0.03125F, 1.71875F, 0.4375F, 12.125F, 6.375F);
		builder.addVertex(0.03125F, 1.71875F, 0.5625F, 12.375F, 6.375F);
		builder.addVertex(0.03125F, 1.46875F, 0.5625F, 12.375F, 6.875F);
		builder.addVertex(0.03125F, 1.46875F, 0.4375F, 12.125F, 6.875F);

		builder.addVertex(-0.21875F, 1.71875F, 0.4375F, 11.625F, 6.375F);
		builder.addVertex(-0.21875F, 1.46875F, 0.4375F, 11.625F, 6.875F);
		builder.addVertex(-0.21875F, 1.46875F, 0.5625F, 11.375F, 6.875F);
		builder.addVertex(-0.21875F, 1.71875F, 0.5625F, 11.375F, 6.375F);

		//standard_back_left2
		builder.addVertex(0.625F, 1.25F, 0.625F, 9.625F, 11.25F);
		builder.addVertex(0.811722F, 1.267059F, 0.624509F, 10F, 11.25F);
		builder.addVertex(0.874174F, 0.585264F, 0.687058F, 10F, 12.625F);
		builder.addVertex(0.687452F, 0.568206F, 0.687549F, 9.625F, 12.625F);

		builder.addVertex(0.623935F, 1.267033F, 0.811722F, 10.75F, 11.25F);
		builder.addVertex(0.686387F, 0.585238F, 0.874271F, 10.75F, 12.625F);
		builder.addVertex(0.873109F, 0.602297F, 0.87378F, 10.375F, 12.625F);
		builder.addVertex(0.810657F, 1.284091F, 0.811231F, 10.375F, 11.25F);

		builder.addVertex(0.687452F, 0.568206F, 0.687549F, 10F, 11.25F);
		builder.addVertex(0.874174F, 0.585264F, 0.687058F, 10.375F, 11.25F);
		builder.addVertex(0.873109F, 0.602297F, 0.87378F, 10.375F, 10.875F);
		builder.addVertex(0.686387F, 0.585238F, 0.874271F, 10F, 10.875F);

		builder.addVertex(0.625F, 1.25F, 0.625F, 9.625F, 11.25F);
		builder.addVertex(0.623935F, 1.267033F, 0.811722F, 9.625F, 10.875F);
		builder.addVertex(0.810657F, 1.284091F, 0.811231F, 10F, 10.875F);
		builder.addVertex(0.811722F, 1.267059F, 0.624509F, 10F, 11.25F);

		builder.addVertex(0.811722F, 1.267059F, 0.624509F, 10F, 11.25F);
		builder.addVertex(0.810657F, 1.284091F, 0.811231F, 10.375F, 11.25F);
		builder.addVertex(0.873109F, 0.602297F, 0.87378F, 10.375F, 12.625F);
		builder.addVertex(0.874174F, 0.585264F, 0.687058F, 10F, 12.625F);

		builder.addVertex(0.625F, 1.25F, 0.625F, 9.625F, 11.25F);
		builder.addVertex(0.687452F, 0.568206F, 0.687549F, 9.625F, 12.625F);
		builder.addVertex(0.686387F, 0.585238F, 0.874271F, 9.25F, 12.625F);
		builder.addVertex(0.623935F, 1.267033F, 0.811722F, 9.25F, 11.25F);

		//standard_back_right2
		builder.addVertex(0.188278F, 1.267059F, 0.624509F, 11.25F, 11.25F);
		builder.addVertex(0.375F, 1.25F, 0.625F, 11.625F, 11.25F);
		builder.addVertex(0.312547F, 0.568206F, 0.687549F, 11.625F, 12.625F);
		builder.addVertex(0.125826F, 0.585264F, 0.687058F, 11.25F, 12.625F);

		builder.addVertex(0.189343F, 1.284091F, 0.811231F, 12.375F, 11.25F);
		builder.addVertex(0.126891F, 0.602297F, 0.87378F, 12.375F, 12.625F);
		builder.addVertex(0.313612F, 0.585238F, 0.874271F, 12F, 12.625F);
		builder.addVertex(0.376065F, 1.267033F, 0.811722F, 12F, 11.25F);

		builder.addVertex(0.125826F, 0.585264F, 0.687058F, 11.625F, 11.25F);
		builder.addVertex(0.312547F, 0.568206F, 0.687549F, 12F, 11.25F);
		builder.addVertex(0.313612F, 0.585238F, 0.874271F, 12F, 10.875F);
		builder.addVertex(0.126891F, 0.602297F, 0.87378F, 11.625F, 10.875F);

		builder.addVertex(0.188278F, 1.267059F, 0.624509F, 11.25F, 11.25F);
		builder.addVertex(0.189343F, 1.284091F, 0.811231F, 11.25F, 10.875F);
		builder.addVertex(0.376065F, 1.267033F, 0.811722F, 11.625F, 10.875F);
		builder.addVertex(0.375F, 1.25F, 0.625F, 11.625F, 11.25F);

		builder.addVertex(0.375F, 1.25F, 0.625F, 11.625F, 11.25F);
		builder.addVertex(0.376065F, 1.267033F, 0.811722F, 12F, 11.25F);
		builder.addVertex(0.313612F, 0.585238F, 0.874271F, 12F, 12.625F);
		builder.addVertex(0.312547F, 0.568206F, 0.687549F, 11.625F, 12.625F);

		builder.addVertex(0.188278F, 1.267059F, 0.624509F, 11.25F, 11.25F);
		builder.addVertex(0.125826F, 0.585264F, 0.687058F, 11.25F, 12.625F);
		builder.addVertex(0.126891F, 0.602297F, 0.87378F, 10.875F, 12.625F);
		builder.addVertex(0.189343F, 1.284091F, 0.811231F, 10.875F, 11.25F);

		//holder_base
		//brazier_edge_back2
		builder.addVertex(0F, 2.006655F, 1.086992F, 0.375F, 10.5F);
		builder.addVertex(1F, 2.006655F, 1.086992F, 2.375F, 10.5F);
		builder.addVertex(1F, 1.846444F, 1.184402F, 2.375F, 10.875F);
		builder.addVertex(0F, 1.846444F, 1.184402F, 0.375F, 10.875F);

		builder.addVertex(0F, 2.104065F, 1.247203F, 4.75F, 10.5F);
		builder.addVertex(0F, 1.943854F, 1.344613F, 4.75F, 10.875F);
		builder.addVertex(1F, 1.943854F, 1.344613F, 2.75F, 10.875F);
		builder.addVertex(1F, 2.104065F, 1.247203F, 2.75F, 10.5F);

		builder.addVertex(0F, 1.846444F, 1.184402F, 2.375F, 10.5F);
		builder.addVertex(1F, 1.846444F, 1.184402F, 4.375F, 10.5F);
		builder.addVertex(1F, 1.943854F, 1.344613F, 4.375F, 10.125F);
		builder.addVertex(0F, 1.943854F, 1.344613F, 2.375F, 10.125F);

		builder.addVertex(0F, 2.006655F, 1.086992F, 0.375F, 10.5F);
		builder.addVertex(0F, 2.104065F, 1.247203F, 0.375F, 10.125F);
		builder.addVertex(1F, 2.104065F, 1.247203F, 2.375F, 10.125F);
		builder.addVertex(1F, 2.006655F, 1.086992F, 2.375F, 10.5F);

		builder.addVertex(1F, 2.006655F, 1.086992F, 2.375F, 10.5F);
		builder.addVertex(1F, 2.104065F, 1.247203F, 2.75F, 10.5F);
		builder.addVertex(1F, 1.943854F, 1.344613F, 2.75F, 10.875F);
		builder.addVertex(1F, 1.846444F, 1.184402F, 2.375F, 10.875F);

		builder.addVertex(0F, 2.006655F, 1.086992F, 0.375F, 10.5F);
		builder.addVertex(0F, 1.846444F, 1.184402F, 0.375F, 10.875F);
		builder.addVertex(0F, 1.943854F, 1.344613F, 0F, 10.875F);
		builder.addVertex(0F, 2.104065F, 1.247203F, 0F, 10.5F);

		//brazier_edge_right2
		builder.addVertex(-0.247203F, 2.104065F, 0F, 2F, 15.5F);
		builder.addVertex(-0.086992F, 2.006655F, 0F, 2.375F, 15.5F);
		builder.addVertex(-0.184402F, 1.846444F, 0F, 2.375F, 15.875F);
		builder.addVertex(-0.344613F, 1.943854F, 0F, 2F, 15.875F);

		builder.addVertex(-0.247203F, 2.104065F, 1F, 4.75F, 15.5F);
		builder.addVertex(-0.344613F, 1.943854F, 1F, 4.75F, 15.875F);
		builder.addVertex(-0.184402F, 1.846444F, 1F, 4.375F, 15.875F);
		builder.addVertex(-0.086992F, 2.006655F, 1F, 4.375F, 15.5F);

		builder.addVertex(-0.344613F, 1.943854F, 0F, 2.375F, 15.5F);
		builder.addVertex(-0.184402F, 1.846444F, 0F, 2.75F, 15.5F);
		builder.addVertex(-0.184402F, 1.846444F, 1F, 2.75F, 13.5F);
		builder.addVertex(-0.344613F, 1.943854F, 1F, 2.375F, 13.5F);

		builder.addVertex(-0.247203F, 2.104065F, 0F, 2F, 15.5F);
		builder.addVertex(-0.247203F, 2.104065F, 1F, 2F, 13.5F);
		builder.addVertex(-0.086992F, 2.006655F, 1F, 2.375F, 13.5F);
		builder.addVertex(-0.086992F, 2.006655F, 0F, 2.375F, 15.5F);

		builder.addVertex(-0.086992F, 2.006655F, 0F, 2.375F, 15.5F);
		builder.addVertex(-0.086992F, 2.006655F, 1F, 4.375F, 15.5F);
		builder.addVertex(-0.184402F, 1.846444F, 1F, 4.375F, 15.875F);
		builder.addVertex(-0.184402F, 1.846444F, 0F, 2.375F, 15.875F);

		builder.addVertex(-0.247203F, 2.104065F, 0F, 2F, 15.5F);
		builder.addVertex(-0.344613F, 1.943854F, 0F, 2F, 15.875F);
		builder.addVertex(-0.344613F, 1.943854F, 1F, 0F, 15.875F);
		builder.addVertex(-0.247203F, 2.104065F, 1F, 0F, 15.5F);

		//brazier_edge_left1
		builder.addVertex(0.966056F, 1.996902F, -0F, 2F, 6.25F);
		builder.addVertex(1.150458F, 2.030846F, -0F, 2.375F, 6.25F);
		builder.addVertex(1.184402F, 1.846444F, -0F, 2.375F, 6.625F);
		builder.addVertex(1F, 1.8125F, -0F, 2F, 6.625F);

		builder.addVertex(0.966056F, 1.996902F, 1F, 4.75F, 6.25F);
		builder.addVertex(1F, 1.8125F, 1F, 4.75F, 6.625F);
		builder.addVertex(1.184402F, 1.846444F, 1F, 4.375F, 6.625F);
		builder.addVertex(1.150458F, 2.030846F, 1F, 4.375F, 6.25F);

		builder.addVertex(1F, 1.8125F, -0F, 2.375F, 6.25F);
		builder.addVertex(1.184402F, 1.846444F, -0F, 2.75F, 6.25F);
		builder.addVertex(1.184402F, 1.846444F, 1F, 2.75F, 4.25F);
		builder.addVertex(1F, 1.8125F, 1F, 2.375F, 4.25F);

		builder.addVertex(0.966056F, 1.996902F, -0F, 2F, 6.25F);
		builder.addVertex(0.966056F, 1.996902F, 1F, 2F, 4.25F);
		builder.addVertex(1.150458F, 2.030846F, 1F, 2.375F, 4.25F);
		builder.addVertex(1.150458F, 2.030846F, -0F, 2.375F, 6.25F);

		builder.addVertex(1.150458F, 2.030846F, -0F, 2.375F, 6.25F);
		builder.addVertex(1.150458F, 2.030846F, 1F, 4.375F, 6.25F);
		builder.addVertex(1.184402F, 1.846444F, 1F, 4.375F, 6.625F);
		builder.addVertex(1.184402F, 1.846444F, -0F, 2.375F, 6.625F);

		builder.addVertex(0.966056F, 1.996902F, -0F, 2F, 6.25F);
		builder.addVertex(1F, 1.8125F, -0F, 2F, 6.625F);
		builder.addVertex(1F, 1.8125F, 1F, 0F, 6.625F);
		builder.addVertex(0.966056F, 1.996902F, 1F, 0F, 6.25F);

		//support_front_right1b
		builder.addVertex(-0.1875F, 1.8125F, -0.186875F, 10.125F, 1.5F);
		builder.addVertex(-0.036816F, 1.924082F, -0.186875F, 10.5F, 1.5F);
		builder.addVertex(0.074766F, 1.773398F, -0.186875F, 10.5F, 1.875F);
		builder.addVertex(-0.075918F, 1.661816F, -0.186875F, 10.125F, 1.875F);

		builder.addVertex(-0.1875F, 1.8125F, 0.000625F, 11.25F, 1.5F);
		builder.addVertex(-0.075918F, 1.661816F, 0.000625F, 11.25F, 1.875F);
		builder.addVertex(0.074766F, 1.773398F, 0.000625F, 10.875F, 1.875F);
		builder.addVertex(-0.036816F, 1.924082F, 0.000625F, 10.875F, 1.5F);

		builder.addVertex(-0.075918F, 1.661816F, -0.186875F, 10.5F, 1.5F);
		builder.addVertex(0.074766F, 1.773398F, -0.186875F, 10.875F, 1.5F);
		builder.addVertex(0.074766F, 1.773398F, 0.000625F, 10.875F, 1.125F);
		builder.addVertex(-0.075918F, 1.661816F, 0.000625F, 10.5F, 1.125F);

		builder.addVertex(-0.1875F, 1.8125F, -0.186875F, 10.125F, 1.5F);
		builder.addVertex(-0.1875F, 1.8125F, 0.000625F, 10.125F, 1.125F);
		builder.addVertex(-0.036816F, 1.924082F, 0.000625F, 10.5F, 1.125F);
		builder.addVertex(-0.036816F, 1.924082F, -0.186875F, 10.5F, 1.5F);

		builder.addVertex(-0.036816F, 1.924082F, -0.186875F, 10.5F, 1.5F);
		builder.addVertex(-0.036816F, 1.924082F, 0.000625F, 10.875F, 1.5F);
		builder.addVertex(0.074766F, 1.773398F, 0.000625F, 10.875F, 1.875F);
		builder.addVertex(0.074766F, 1.773398F, -0.186875F, 10.5F, 1.875F);

		builder.addVertex(-0.1875F, 1.8125F, -0.186875F, 10.125F, 1.5F);
		builder.addVertex(-0.075918F, 1.661816F, -0.186875F, 10.125F, 1.875F);
		builder.addVertex(-0.075918F, 1.661816F, 0.000625F, 9.75F, 1.875F);
		builder.addVertex(-0.1875F, 1.8125F, 0.000625F, 9.75F, 1.5F);

	}
}
